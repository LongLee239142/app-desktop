package atlantafx.sampler.base.service.cashier;


import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.entity.common.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableCoffeeService {

  private static List<Tables> tablesList = new ArrayList<Tables>();

  public static List<Tables> getAllTable() {
    List<Tables> tables = new ArrayList<>();
    String sql = "SELECT * FROM tables";
    try (Connection connection = JDBCConnect.getJDBCConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
        sql); ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {

        Tables table = new Tables(resultSet.getString("name"),
            resultSet.getInt("status_id"));
        tables.add(table);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tables;
  }
  public static boolean updateStatusTable(int status, String tableName){
    String sql = "UPDATE tables SET status_id = ? WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, status);
      preparedStatement.setString(2, tableName);
      preparedStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  public static Tables getTableByName(String tableName) {
    Tables table = null;
    String sql = "SELECT * FROM tables WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, tableName);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        table = new Tables(resultSet.getString("name"), resultSet.getInt("status_id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return table;
  }
  // Method to get the count of tables matching the search keyword
  public static int getFilteredTableCount(int currentPage, int itemsPerPage, String currentKeyword) {
    // Get all tables without pagination to count them accurately
    List<Tables> allTables = TableCoffeeService.getAllTable();

    if (currentKeyword.isEmpty()) {
      return allTables.size(); // Return the total count if no keyword is provided
    }

    // Filter tables by keyword and count the matches
    return (int) allTables.stream()
        .filter(table -> table.getName().toLowerCase().contains(currentKeyword.toLowerCase()))
        .count();
  }



  // Method to retrieve table names based on current keyword, page, and page size
  public static ArrayList<String> getNameTable(int page, int pageSize, String keyword) {
    List<Tables> allTables = TableCoffeeService.getAllTable();
    ArrayList<String> filteredTables = new ArrayList<>();

    // Filter tables by keyword if it's not empty
    if (!keyword.isEmpty()) {
      for (Tables table : allTables) {
        if (table.getName().toLowerCase().contains(keyword.toLowerCase())) {
          filteredTables.add(table.getName());
        }
      }
    } else {
      for (Tables table : allTables) {
        filteredTables.add(table.getName());
      } // No filtering needed
    }

    // Sort the filtered tables in ascending order
    Collections.sort(filteredTables, Comparator.naturalOrder());

    // Calculate start and end indices for pagination
    int start = (page - 1) * pageSize;
    int end = Math.min(start + pageSize, filteredTables.size());

    // Return the paginated list of tables
    return new ArrayList<>(filteredTables.subList(start, end));
  }






  public static int getStatusByTableName(String tableName) {
    Tables table = getTableByName(tableName);
    if (table!= null) {
      return table.getStatusId();
    } else {
      return -1; // Or throw an exception or handle the case differently
    }
  }
  public static boolean deleteTableByName(String tableName){
    String sql = "DELETE FROM tables WHERE name =?";
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, tableName);
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean addNewTable(String tableName) {
    String sql = "INSERT INTO tables (name, status_id) VALUES (?, 2)";
    try (Connection connection = JDBCConnect.getJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, tableName);
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
