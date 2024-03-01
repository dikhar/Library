package org.framework;

import lombok.*;

import java.time.Duration;
import java.util.Map;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpringSessionData {

    private String id;
    private Duration maxInactiveInterval;
    private Map<String, Object> attributes;
    private long createdMillis;
}
