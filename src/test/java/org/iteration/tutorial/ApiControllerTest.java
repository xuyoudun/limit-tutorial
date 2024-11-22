package org.iteration.tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final int REQUESTS_PER_SECOND = 500;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testHighConcurrencyRequests() throws Exception {
        // 模拟多个线程进行请求
        int totalRequests = REQUESTS_PER_SECOND * 3;
        int concurrentThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);

        // 模拟高并发请求
        for (int i = 0; i < totalRequests; i++) {
            int requestIndex = i % 3; // 轮流调用 api1, api2, api3
            executorService.submit(() -> {
                try {
                    switch (requestIndex) {
                        case 0:
                            // API1: GET
                            mockMvc.perform(MockMvcRequestBuilders.get("/limit/api1")
                                            .param("userId", "user1")
                                            .accept(MediaType.APPLICATION_JSON))
                                    .andDo(MockMvcResultHandlers.print())
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andExpect(MockMvcResultMatchers.content().string("Request successful!"));
                            break;
                        case 1:
                            // API2: POST
                            mockMvc.perform(MockMvcRequestBuilders.post("/limit/api2")
                                            .param("userId", "user2")
                                            .accept(MediaType.APPLICATION_JSON))
                                    .andDo(MockMvcResultHandlers.print())
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andExpect(MockMvcResultMatchers.content().string("Request successful!"));
                            break;
                        case 2:
                            // API3: PUT
                            mockMvc.perform(MockMvcRequestBuilders.put("/limit/api3")
                                            .param("userId", "user3")
                                            .accept(MediaType.APPLICATION_JSON))
                                    .andDo(MockMvcResultHandlers.print())
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andExpect(MockMvcResultMatchers.content().string("Request successful!"));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

}
