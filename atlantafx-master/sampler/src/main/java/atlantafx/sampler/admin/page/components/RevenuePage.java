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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public final class RevenuePage extends OutlinePage {
    public static final String NAME = "Revenue by Date";

    private Text billCountText;
    private Text revenueText;
    private BarChart<String, Number> barChart;

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
        VBox billCountCard = createCard("Today's Bill Count", "0");
        billCountText = (Text) billCountCard.getChildren().get(1);
        grid.add(billCountCard, 0, 0);

        // Card for Today's Revenue
        VBox revenueCard = createCard("Today's Revenue", "0");
        revenueText = (Text) revenueCard.getChildren().get(1);
        grid.add(revenueCard, 1, 0);

        // Bar chart for daily revenue in the month
        barChart = createBarChart();
        loadBarChartData();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(barChart);
        borderPane.setTop(grid);
       getChildren().add(borderPane);
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefSize(200, 100);
        card.getChildren().addAll(new Text(title), new Text(value));
        return card;
    }

    private BarChart<String, Number> createBarChart() {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue");

        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), yAxis);
        chart.setTitle("Daily Revenue for This Month");
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

                billCountText.setText(String.valueOf(billCount)); // Cập nhật số lượng hóa đơn
                revenueText.setText(String.format("%.2f", totalRevenue)); // Cập nhật doanh thu
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBarChartData() {
        String query = "SELECT DATE(created_at) AS order_date, SUM(total_amount) AS total_revenue " +
                "FROM bill_order WHERE MONTH(created_at) = MONTH(CURRENT_DATE) " +
                "GROUP BY DATE(created_at)";

        try (Connection connection = JDBCConnect.getJDBCConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (resultSet.next()) {
                String date = resultSet.getDate("order_date").toString();
                double revenue = resultSet.getDouble("total_revenue");
                series.getData().add(new XYChart.Data<>(date, revenue));
            }

            barChart.getData().add(series);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
