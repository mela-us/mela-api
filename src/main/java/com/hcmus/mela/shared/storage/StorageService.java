package com.hcmus.mela.shared.storage;

import java.util.Map;

public interface StorageService {

    Map<String, String> getUploadUserImagePreSignedUrl(String fileName);

    Map<String, String> getUploadConversationFilePreSignedUrl(String fileName);

    Map<String, String> getUploadUserFilePreSignedUrl(String fileName);

    Map<String, String> getUploadAdminFilePreSignedUrl(String fileName);
}
