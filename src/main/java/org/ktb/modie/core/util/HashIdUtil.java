package org.ktb.modie.core.util;

import org.hashids.Hashids;
import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashIdUtil {

    private final Hashids hashids;

    public HashIdUtil(@Value("${hashid.salt}") String salt) {
        this.hashids = new Hashids(salt, 10);
    }

    public String encode(Long id) {
        return hashids.encode(id);
    }

    public Long decode(String hash) {
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new BusinessException(CustomErrorCode.MEETING_NOT_FOUND);
        }
        return decoded[0];
    }
}
