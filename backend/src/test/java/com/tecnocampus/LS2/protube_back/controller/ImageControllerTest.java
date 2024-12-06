package com.tecnocampus.LS2.protube_back.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.TestPropertySource;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
@PropertySource("classpath:application.properties")  // Ensure properties are loaded
@TestPropertySource(properties = "pro_tube.store.dir=/home/samur18/protube/store")  // Use the actual property or system variable
public class ImageControllerTest {

    @InjectMocks
    private ImageController imageController;

    private MockMvc mockMvc;

    @Value("${pro_tube.store.dir}")
    private String storeDir;  // Inject the actual property value

    @BeforeEach
    public void setup() {
        // Pass the actual store directory to the controller constructor
        imageController = new ImageController(storeDir);

        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    public void testServeFile() throws Exception {
        // Use the actual file path from the store directory
        Path file = Paths.get(storeDir).resolve("0.webp");

        // Perform the request and verify the result
        mockMvc.perform(get("/api/images/0.webp"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"0.webp\""));
    }
}
