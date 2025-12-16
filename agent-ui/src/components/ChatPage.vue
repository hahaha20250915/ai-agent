<template>
  <div class="chat-container">
    <div class="messages">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <span class="role">{{ msg.role === 'user' ? '我' : 'AI' }}：</span>
        <div class="message-content">
          <!-- 显示图片 -->
          <div v-if="msg.images && msg.images.length > 0" class="message-images">
            <img
              v-for="(img, imgIndex) in msg.images"
              :key="imgIndex"
              :src="img.preview"
              :alt="img.name"
              class="message-image"
              @click="previewImage(img.preview)"
            />
          </div>
          <!-- 显示PDF文件 -->
          <div v-if="msg.pdfs && msg.pdfs.length > 0" class="message-files">
            <div
              v-for="(pdf, pdfIndex) in msg.pdfs"
              :key="pdfIndex"
              class="message-file"
            >
              📄 {{ pdf.name }}
            </div>
          </div>
          <!-- 显示文本 -->
          <span class="text">{{ msg.text }}</span>
        </div>
      </div>
    </div>

    <!-- 文件预览区域 -->
    <div v-if="selectedImages.length > 0 || selectedPdfs.length > 0" class="file-preview">
      <div class="preview-title">已选择文件：</div>
      <div class="preview-items">
        <!-- 图片预览 -->
        <div
          v-for="(img, index) in selectedImages"
          :key="index"
          class="preview-item"
        >
          <img :src="img.preview" :alt="img.name" class="preview-image" />
          <span class="preview-name">{{ img.name }}</span>
          <button class="remove-btn" @click="removeImage(index)">×</button>
        </div>
        <!-- PDF预览 -->
        <div
          v-for="(pdf, index) in selectedPdfs"
          :key="`pdf-${index}`"
          class="preview-item"
        >
          <div class="preview-file">📄</div>
          <span class="preview-name">{{ pdf.name }}</span>
          <button class="remove-btn" @click="removePdf(index)">×</button>
        </div>
      </div>
    </div>

    <form class="input-area" @submit.prevent="handleSend">
      <div class="input-wrapper">
        <input
          v-model="input"
          placeholder="请输入你的问题..."
          :disabled="loading"
        />
        <div class="file-buttons">
          <label class="file-button" :class="{ disabled: loading }">
            <input
              type="file"
              accept="image/*"
              multiple
              @change="handleImageSelect"
              :disabled="loading"
              style="display: none"
            />
            📷 图片
          </label>
          <label class="file-button" :class="{ disabled: loading }">
            <input
              type="file"
              accept="application/pdf"
              multiple
              @change="handlePdfSelect"
              :disabled="loading"
              style="display: none"
            />
            📄 PDF
          </label>
        </div>
      </div>
      <button
        type="submit"
        :disabled="loading || (!input.trim() && selectedImages.length === 0 && selectedPdfs.length === 0)"
      >
        {{ loading ? '等待回复...' : '发送' }}
      </button>
    </form>

    <!-- 图片预览弹窗 -->
    <div v-if="previewImageUrl" class="image-modal" @click="previewImageUrl = null">
      <img :src="previewImageUrl" class="modal-image" @click.stop />
      <button class="modal-close" @click="previewImageUrl = null">×</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const input = ref('');
const messages = ref([]);
const loading = ref(false);
const selectedImages = ref([]);
const selectedPdfs = ref([]);
const previewImageUrl = ref(null);
let abortController = null;

// 处理图片选择
const handleImageSelect = (event) => {
  const files = Array.from(event.target.files);
  files.forEach((file) => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = (e) => {
        selectedImages.value.push({
          file,
          name: file.name,
          preview: e.target.result
        });
      };
      reader.readAsDataURL(file);
    }
  });
  event.target.value = ''; // 重置input，允许重复选择同一文件
};

// 处理PDF选择
const handlePdfSelect = (event) => {
  const files = Array.from(event.target.files);
  files.forEach((file) => {
    if (file.type === 'application/pdf') {
      selectedPdfs.value.push({
        file,
        name: file.name
      });
    }
  });
  event.target.value = ''; // 重置input
};

// 移除图片
const removeImage = (index) => {
  selectedImages.value.splice(index, 1);
};

// 移除PDF
const removePdf = (index) => {
  selectedPdfs.value.splice(index, 1);
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
};

// 发送消息
const handleSend = async () => {
  const text = input.value.trim();
  const hasImages = selectedImages.value.length > 0;
  const hasPdfs = selectedPdfs.value.length > 0;

  // 至少需要有文本或文件
  if (!text && !hasImages && !hasPdfs) return;
  if (loading.value) return;

  // 取消之前的请求
  if (abortController) {
    abortController.abort();
  }

  // 记录用户消息
  const userMessage = {
    role: 'user',
    text: text || '(已上传文件)',
    images: hasImages
      ? selectedImages.value.map((img) => ({
          name: img.name,
          preview: img.preview
        }))
      : [],
    pdfs: hasPdfs
      ? selectedPdfs.value.map((pdf) => ({ name: pdf.name }))
      : []
  };
  messages.value.push(userMessage);

  // 清空输入和文件
  input.value = '';
  const imagesToSend = [...selectedImages.value];
  const pdfsToSend = [...selectedPdfs.value];
  selectedImages.value = [];
  selectedPdfs.value = [];

  // 为当前回答占位
  const aiMessage = { role: 'ai', text: '' };
  messages.value.push(aiMessage);

  loading.value = true;
  abortController = new AbortController();

  try {
    // 判断是否有文件上传，有则使用多模态接口，否则使用普通接口
    if (hasImages || hasPdfs) {
      await sendMultimodalMessage(text, imagesToSend, pdfsToSend, aiMessage);
    } else {
      await sendTextMessage(text, aiMessage);
    }
  } catch (error) {
    if (error.name !== 'AbortError') {
      console.error('发送消息失败:', error);
      aiMessage.text = '发送失败，请重试。';
    }
  } finally {
    loading.value = false;
    abortController = null;
  }
};

// 发送文本消息（使用原来的接口）
const sendTextMessage = async (text, aiMessage) => {
  const params = new URLSearchParams({
    memoryId: 'default',
    message: text
  });

  const response = await fetch(`/ai/chat?${params.toString()}`, {
    method: 'GET',
    signal: abortController.signal,
    headers: {
      Accept: 'text/event-stream'
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split('\n');
    buffer = lines.pop() || ''; // 保留最后不完整的行

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const data = line.substring(6).trim();
        if (data && data !== '[DONE]') {
          aiMessage.text += data;
        }
      }
    }
  }

  // 处理剩余的缓冲区
  if (buffer) {
    if (buffer.startsWith('data: ')) {
      const data = buffer.substring(6).trim();
      if (data && data !== '[DONE]') {
        aiMessage.text += data;
      }
    }
  }
};

// 发送多模态消息
const sendMultimodalMessage = async (text, images, pdfs, aiMessage) => {
  const formData = new FormData();
  formData.append('memoryId', 'default');
  if (text) {
    formData.append('message', text);
  }

  // 添加图片
  images.forEach((img) => {
    formData.append('images', img.file);
  });

  // 添加PDF
  pdfs.forEach((pdf) => {
    formData.append('pdfs', pdf.file);
  });

  const response = await fetch('/ai/chat/multimodal', {
    method: 'POST',
    body: formData,
    signal: abortController.signal,
    headers: {
      Accept: 'text/event-stream'
    }
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split('\n');
    buffer = lines.pop() || ''; // 保留最后不完整的行

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const data = line.substring(6).trim();
        if (data && data !== '[DONE]') {
          aiMessage.text += data;
        }
      }
    }
  }

  // 处理剩余的缓冲区
  if (buffer) {
    if (buffer.startsWith('data: ')) {
      const data = buffer.substring(6).trim();
      if (data && data !== '[DONE]') {
        aiMessage.text += data;
      }
    }
  }
};
</script>

<style scoped>
.chat-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 80vh;
  position: relative;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.message {
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.5;
}

.message.user {
  background: #e6f3ff;
  align-self: flex-end;
  margin-left: auto;
  max-width: 80%;
}

.message.ai {
  background: #f5f5f5;
  align-self: flex-start;
  max-width: 80%;
}

.role {
  font-weight: bold;
  margin-right: 8px;
  display: block;
  margin-bottom: 4px;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 4px;
  cursor: pointer;
  object-fit: cover;
}

.message-files {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.message-file {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 4px;
  font-size: 14px;
}

.text {
  word-wrap: break-word;
  white-space: pre-wrap;
}

.file-preview {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.preview-title {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.preview-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.preview-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.preview-file {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  background: #f5f5f5;
  border-radius: 4px;
}

.preview-name {
  margin-top: 4px;
  font-size: 12px;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: none;
  background: #ff4444;
  color: white;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  background: #cc0000;
}

.input-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: center;
}

.input-wrapper input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
}

.file-buttons {
  display: flex;
  gap: 4px;
}

.file-button {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 13px;
  user-select: none;
  white-space: nowrap;
}

.file-button:hover:not(.disabled) {
  background: #e0e0e0;
}

.file-button.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.input-area button {
  padding: 10px 18px;
  border-radius: 8px;
  border: none;
  background: #409eff;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  align-self: flex-end;
}

.input-area button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  cursor: pointer;
}

.modal-image {
  max-width: 90%;
  max-height: 90%;
  border-radius: 8px;
  cursor: default;
}

.modal-close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  cursor: pointer;
  font-size: 24px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  background: white;
}

/* 滚动条样式 */
.messages::-webkit-scrollbar {
  width: 6px;
}

.messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.messages::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.messages::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>

