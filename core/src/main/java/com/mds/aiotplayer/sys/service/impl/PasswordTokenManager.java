package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.model.User;

public interface PasswordTokenManager {

    /**
     * {@inheritDoc}
     */
    String generateRecoveryToken(User user);

    /**
     * {@inheritDoc}
     */
    boolean isRecoveryTokenValid(User user, String token);

    void invalidateRecoveryToken(User user, String token);
}