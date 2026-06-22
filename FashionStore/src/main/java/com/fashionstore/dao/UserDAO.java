package com.fashionstore.dao;

import com.fashionstore.model.User;

public interface UserDAO {

    boolean registerUser(User user);

    User loginUser(String email, String password);

    boolean isEmailExists(String email);

    User getUserById(int userId);

    boolean updateUser(User user);

    boolean deleteUser(int userId);
}