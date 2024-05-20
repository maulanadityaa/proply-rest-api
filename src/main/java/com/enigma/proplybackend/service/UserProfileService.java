package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.UserProfileRequest;
import com.enigma.proplybackend.model.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse uploadImage(UserProfileRequest userProfileRequest);

    UserProfileResponse getByUserId(String userId);
}
