package com.wirebarly;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {
}
