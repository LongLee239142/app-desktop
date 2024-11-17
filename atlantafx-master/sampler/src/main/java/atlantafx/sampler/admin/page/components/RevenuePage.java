package atlantafx.sampler.admin.page.components;

import atlantafx.sampler.admin.page.OutlinePage;
import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ComboBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class RevenuePage extends OutlinePage {
    public static final String NAME = "Doanh thu theo ngày";

    private Text billCountText;
    private Text revenueText;
    private BarChart<String, Number> barChart;
    private ComboBox<String> monthFilterComboBox;

    @Override
    public String getName() {
        return NAME;
    }

    public RevenuePage() {
        super();
        initializeUI();
        loadRevenueData();
    }

    private void initializeUI() {
        // Create a grid for the cards
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setStyle("-fx-padding: 20;");

        // Card for Today's Bill Count
        VBox billCountCard = createCard("Số lượng hóa đơn hôm nay", "0", "#1e90ff");
        billCountText = (Text) billCountCard.getChildren().get(1);
        grid.add(billCountCard, 0, 0);

        // Card for Today's Revenue
        VBox revenueCard = createCard("Doanh thu hôm nay", "0", "#1e90ff");
        revenueText = (Text) revenueCard.getChildren().get(1);
        grid.add(revenueCard, 1, 0);

        // Month filter
        monthFilterComboBox = new ComboBox<>();
        // Add months from January to December
        for (int i = 1; i <= 12; i++) {
            monthFilterComboBox.getItems().add("Tháng " + i);
        }
        int currentMonth = LocalDate.now().getMonthValue();
        monthFilterComboBox.getSelectionModel().select("Tháng " + currentMonth); // Default to current month
        monthFilterComboBox.setOnAction(e -> loadBarChartData());

        grid.add(monthFilterComboBox, 0, 1, 2, 1);

        // Bar chart for daily revenue
        barChart = createBarChart();
        loadBarChartData();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(barChart);
        borderPane.setTop(grid);
        getChildren().add(borderPane);
    }

    private VBox createCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefSize(200, 100);
        card.getChildren().addAll(new Text(title), new Text(value));
        return card;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Ngày");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu ");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Doanh thu hàng ngày trong tháng");
        return chart;
    }

    private void loadRevenueData() {
        String query = "SELECT COUNT(*) AS bill_count, SUM(total_amount) AS total_revenue " +
                "FROM bill_order WHERE DATE(created_at) = ?";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, LocalDate.now().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int billCount = resultSet.getInt("bill_count");
                double totalRevenue = resultSet.getDouble("total_revenue");

                billCountText.setText(String.valueOf(billCount));
                revenueText.setText(String.format("%.2f", totalRevenue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBarChartData() {
        int selectedMonth = Integer.parseInt(monthFilterComboBox.getValue().split(" ")[1]);
        String query = "SELECT DATE(created_at) AS order_date, COUNT(*) AS bill_count, SUM(total_amount) AS total_revenue " +
                "FROM bill_order WHERE MONTH(created_at) = ? " +
                "GROUP BY DATE(created_at)";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, selectedMonth);
            ResultSet resultSet = statement.executeQuery();

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Doanh thu");
//            XYChart.Series<String, Number> billCountSeries = new XYChart.Series<>();
//            billCountSeries.setName("Số hóa đơn");

            while (resultSet.next()) {
                String date = resultSet.getDate("order_date").toString();
                int billCount = resultSet.getInt("bill_count");
                double totalRevenue = resultSet.getDouble("total_revenue");

                revenueSeries.getData().add(new XYChart.Data<>(date, totalRevenue));
//                billCountSeries.getData().add(new XYChart.Data<>(date, billCount));
            }

            barChart.getData().clear();
            barChart.getData().addAll(revenueSeries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
