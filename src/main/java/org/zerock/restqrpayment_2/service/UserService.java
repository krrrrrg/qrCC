package org.zerock.restqrpayment_2.service;

public interface UserService {
    void changePassword(String phone, String currentPassword, String newPassword);
    void deleteAccount(String phone, String password);
}
