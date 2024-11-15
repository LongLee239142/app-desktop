package atlantafx.sampler.base.service.admin;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.admin.Admin;
import atlantafx.sampler.base.entity.cashier.Cashier;
import atlantafx.sampler.base.entity.common.StoreInfo;
import atlantafx.sampler.base.util.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsService {
    public Admin getAdminInfo() {
        Admin admin = null;
        String query = "SELECT username, email FROM admin WHERE id = 1"; // Assuming the logged-in admin has id=1
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                admin = new Admin(username, email);  // Assuming an Admin class with username & email fields
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }

    public Cashier getCashierInfo() {
        Cashier cashier = null;
        String query = "SELECT username FROM cashier WHERE id = 1"; // Assuming the logged-in cashier has id=1
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                cashier = new Cashier(username);  // Assuming a Cashier class with a username field
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cashier;
    }

    public StoreInfo getStoreInfo() {
        StoreInfo storeInfo = null;
        String query = "SELECT store_name, store_email, phone, store_address, tax_code FROM store_info WHERE id = 1"; // Assuming only one store info
        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            if (resultSet.next()) {
                String storeName = resultSet.getString("store_name");
                String storeEmail = resultSet.getString("store_email");
                String phone = resultSet.getString("phone");
                String storeAddress = resultSet.getString("store_address");
                String taxCode = resultSet.getString("tax_code");

                storeInfo = new StoreInfo(storeName, storeEmail, phone, storeAddress, taxCode);  // Assuming a StoreInfo class with the necessary fields
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storeInfo;
    }


    // Update Admin Information
    public boolean updateAdminInfo(int adminId, String username, String email, String currentPassword, String newPassword) {
        String query = "UPDATE admin SET username = ?, email = ?, password_hash = ? WHERE id = ?";
        boolean isUpdated = false;

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Validate the current password here (for example using bcrypt)
            if (PasswordUtils.checkPassword(currentPassword, getAdminPasswordById(adminId))) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, PasswordUtils.hashPassword(newPassword));  // Assuming newPassword is hashed
                stmt.setInt(4, adminId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    isUpdated = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    // Update Cashier Information
    public boolean updateCashierInfo(int cashierId, String username, String currentPassword, String newPassword) {
        String query = "UPDATE cashier SET username = ?, password_hash = ? WHERE id = ?";
        boolean isUpdated = false;

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Validate the current password here
            if (PasswordUtils.checkPassword(currentPassword, getCashierPasswordById(cashierId))) {
                stmt.setString(1, username);
                stmt.setString(2, PasswordUtils.hashPassword(newPassword));  // Assuming newPassword is hashed
                stmt.setInt(3, cashierId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    isUpdated = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    // Update Store Information
    public boolean updateStoreInfo(int storeId, String storeName, String storeEmail, String phone, String storeAddress, String taxCode) {
        String query = "UPDATE store_info SET store_name = ?, store_email = ?, phone = ?, store_address = ?, tax_code = ? WHERE id = ?";
        boolean isUpdated = false;

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, storeName);
            stmt.setString(2, storeEmail);
            stmt.setString(3, phone);
            stmt.setString(4, storeAddress);
            stmt.setString(5, taxCode);
            stmt.setInt(6, storeId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                isUpdated = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    // Helper method to fetch the current admin password (hashed)
    private String getAdminPasswordById(int adminId) {
        String passwordHash = null;
        String query = "SELECT password_hash FROM admin WHERE id = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, adminId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                passwordHash = resultSet.getString("password_hash");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwordHash;
    }

    // Helper method to fetch the current cashier password (hashed)
    private String getCashierPasswordById(int cashierId) {
        String passwordHash = null;
        String query = "SELECT password_hash FROM cashier WHERE id = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, cashierId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                passwordHash = resultSet.getString("password_hash");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwordHash;
    }
}
