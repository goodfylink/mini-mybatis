package org.apache.ibatis.mapper;

import java.util.List;

/**
 * User Mapper Interface
 *
 * @author jcyin
 * @since 2026/2/21
 */
public interface UserMapper {

    User selectUserById(int id);

    List<User> selectAllUsers();

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    Integer countUsers();

    User getUserByMap(java.util.Map<String, Object> map);

    List<User> getUsersByName(String name);
}