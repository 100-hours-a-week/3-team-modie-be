package org.ktb.modie.core.util;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
public class HashIdUtil {

    private final Hashids hashids = new Hashids("MODiE-secret-salt", 8); // salt, 최소 길이

    public String encode(Long id) {
        return hashids.encode(id);
    }

    public Long decode(String hash) {
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0)
            throw new IllegalArgumentException("Invalid hash id");
        return decoded[0];
    }
}
