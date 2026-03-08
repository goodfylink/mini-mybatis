package org.apache.ibatis.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for UserMapper
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class UserMapperTest {

    private SqlSessionFactory sqlSessionFactory;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();

        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), age INT)");
        stmt.execute("DELETE FROM users");
        stmt.execute("INSERT INTO users (name, age) VALUES ('Alice', 25)");
        stmt.execute("INSERT INTO users (name, age) VALUES ('Bob', 30)");
        stmt.close();
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testSelectUserById() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectUserById(1);
            System.out.println(user);
            assertNotNull(user);
            assertEquals(1, user.getId());
            assertEquals("Alice", user.getName());
            assertEquals(25, user.getAge());
        }
    }

    @Test
    public void testSelectAllUsers() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectAllUsers();
            assertEquals(2, users.size());
        }
    }

    @Test
    public void testInsertUser() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User newUser = new User(0, "Charlie", 35);
            mapper.insertUser(newUser);
            session.commit();

            User inserted = mapper.selectUserById(3); // Assuming auto-increment
            assertNotNull(inserted);
            assertEquals("Charlie", inserted.getName());
        }
    }

    @Test
    public void testUpdateUser() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectUserById(1);
            user.setAge(26);
            mapper.updateUser(user);
            session.commit();

            User updated = mapper.selectUserById(1);
            assertEquals(26, updated.getAge());
        }
    }

    @Test
    public void testDeleteUser() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.deleteUser(1);
            session.commit();

            User deleted = mapper.selectUserById(1);
            assertNull(deleted);
        }
    }

    @Test
    public void testCountUsers() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            Integer count = mapper.countUsers();
            assertEquals(Integer.valueOf(2), count);
        }
    }

    @Test
    public void testSelectByMap() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("name", "Alice");
            params.put("age", 25);
            User user = mapper.getUserByMap(params);
            assertNotNull(user);
            assertEquals("Alice", user.getName());
        }
    }

    @Test
    public void testSelectByName() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.getUsersByName("Ali%");
            assertEquals(1, users.size());
            assertEquals("Alice", users.get(0).getName());
        }
    }
}