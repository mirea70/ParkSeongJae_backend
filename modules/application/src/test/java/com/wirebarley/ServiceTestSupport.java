package com.wirebarley;

import com.wirebarley.out.common.IdGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class ServiceTestSupport {
    @Mock
    protected IdGenerator idGenerator;
}
