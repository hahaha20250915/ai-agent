package com.agent.longchain;

import reactor.core.publisher.Flux;

public interface Assistant {
    
    Flux<String> chat(String message);
}
