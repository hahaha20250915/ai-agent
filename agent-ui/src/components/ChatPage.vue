<template>
  <div class="chat-container">
    <div class="messages">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <span class="role">{{ msg.role === 'user' ? '我' : 'AI' }}：</span>
        <span class="text">{{ msg.text }}</span>
      </div>
    </div>

    <form class="input-area" @submit.prevent="handleSend">
      <input
        v-model="input"
        placeholder="请输入你的问题..."
        :disabled="loading"
      />
      <button type="submit" :disabled="loading || !input.trim()">
        {{ loading ? '等待回复...' : '发送' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const input = ref('');
const messages = ref([]);
const loading = ref(false);
let source = null;

const handleSend = () => {
  const text = input.value.trim();
  if (!text || loading.value) return;

  // 记录用户消息
  messages.value.push({ role: 'user', text });
  input.value = '';

  // 为当前回答占位
  const aiMessage = { role: 'ai', text: '' };
  messages.value.push(aiMessage);

  // 关闭之前的连接
  if (source) {
    source.close();
    source = null;
  }

  loading.value = true;

  const params = new URLSearchParams({
    memoryId: 'default',
    message: text
  });

  // 使用 Vite 代理，直接访问 /ai/chat
  source = new EventSource(`/ai/chat?${params.toString()}`);

  source.onmessage = (event) => {
    if (event.data) {
      aiMessage.text += event.data;
    }
  };

  source.onerror = () => {
    loading.value = false;
    if (source) {
      source.close();
      source = null;
    }
  };

  source.onopen = () => {
    loading.value = true;
  };

  // 简单超时保护：30 秒后自动关闭
  setTimeout(() => {
    if (source) {
      source.close();
      source = null;
      loading.value = false;
    }
  }, 30000);
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
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.message {
  margin-bottom: 10px;
  padding: 8px 10px;
  border-radius: 8px;
  line-height: 1.5;
}

.message.user {
  background: #e6f3ff;
  align-self: flex-end;
}

.message.ai {
  background: #f5f5f5;
  align-self: flex-start;
}

.role {
  font-weight: bold;
  margin-right: 4px;
}

.input-area {
  display: flex;
  gap: 8px;
}

.input-area input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
}

.input-area button {
  padding: 10px 18px;
  border-radius: 8px;
  border: none;
  background: #409eff;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
}

.input-area button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>


