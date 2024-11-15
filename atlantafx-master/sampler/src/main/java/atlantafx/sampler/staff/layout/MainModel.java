package atlantafx.sampler.staff.layout;

import atlantafx.sampler.staff.event.DefaultEventBus;
import atlantafx.sampler.staff.event.NavEvent;
import atlantafx.sampler.staff.page.Page;
import atlantafx.sampler.staff.page.components.*;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static atlantafx.sampler.staff.layout.MainModel.SubLayer.PAGE;
import static atlantafx.sampler.staff.layout.MainModel.SubLayer.SOURCE_CODE;

public class MainModel {

    public static final Class<? extends Page> DEFAULT_PAGE = WorkSchedulePage.class;

    private static final Map<Class<? extends Page>, NavTree.Item> NAV_TREE = createNavItems();

    public enum SubLayer {
        PAGE,
        SOURCE_CODE
    }

    public NavTree.Item getTreeItemForPage(Class<? extends Page> pageClass) {
        return NAV_TREE.getOrDefault(pageClass, NAV_TREE.get(DEFAULT_PAGE));
    }

    List<NavTree.Item> findPages(String filter) {
        return NAV_TREE.values().stream()
                .filter(item -> item.getValue() != null && item.getValue().matches(filter))
                .toList();
    }

    public MainModel() {
        DefaultEventBus.getInstance().subscribe(NavEvent.class, e -> navigate(e.getPage()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    // ~
    private final ReadOnlyObjectWrapper<Class<? extends Page>> selectedPage = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<Class<? extends Page>> selectedPageProperty() {
        return selectedPage.getReadOnlyProperty();
    }

    // ~
    private final ReadOnlyObjectWrapper<SubLayer> currentSubLayer = new ReadOnlyObjectWrapper<>(PAGE);

    public ReadOnlyObjectProperty<SubLayer> currentSubLayerProperty() {
        return currentSubLayer.getReadOnlyProperty();
    }

    // ~
    private final ReadOnlyObjectWrapper<NavTree.Item> navTree = new ReadOnlyObjectWrapper<>(createTree());

    public ReadOnlyObjectProperty<NavTree.Item> navTreeProperty() {
        return navTree.getReadOnlyProperty();
    }

    private NavTree.Item createTree() {
        // Bàn group
        var tables = NavTree.Item.group("Bàn", new FontIcon(Material2OutlinedMZ.TABLET));
        tables.getChildren().setAll(
                NAV_TREE.get(TableListPage.class) // Danh sách các bàn
        );

        // Công Và Lương group
        var salary = NavTree.Item.group("Công Và Lương", new FontIcon(Material2OutlinedMZ.PAYMENTS));
        salary.getChildren().setAll(
//                NAV_TREE.get(WorkSchedulePage.class),        // Lịch làm việc
               NAV_TREE.get(TimeAttendancePage.class),      // Chấm công
//                NAV_TREE.get(WorkExplanationPage.class),     // Giai trình công
//                NAV_TREE.get(LeaveRequestPage.class),        // Xin nghỉ phép
                NAV_TREE.get(SalaryReportPage.class)         // Phiếu báo lương
        );

        // Thông tin cá nhân group
        var personalInfo = NavTree.Item.group("Thông tin cá nhân", new FontIcon(Material2OutlinedMZ.PEOPLE));
        personalInfo.getChildren().setAll(
                NAV_TREE.get(ChangePasswordPage.class),      // Đổi mật khẩu
                NAV_TREE.get(UpdatePersonalInfoPage.class)   // Thay đổi thông tin cá nhân
        );

        // Add all categories to the root navigation tree
        var root = NavTree.Item.root();
        root.getChildren().setAll(
                tables,
                salary,
                personalInfo
        );

        return root;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Nav Tree                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public static Map<Class<? extends Page>, NavTree.Item> createNavItems() {
        var map = new HashMap<Class<? extends Page>, NavTree.Item>();

        // Bàn
        map.put(TableListPage.class, NavTree.Item.page("Danh sách các bàn", TableListPage.class));

        // Công Và Lương
//        map.put(WorkSchedulePage.class, NavTree.Item.page("Lịch làm việc", WorkSchedulePage.class));
      map.put(TimeAttendancePage.class, NavTree.Item.page("Chấm công", TimeAttendancePage.class));
//        map.put(WorkExplanationPage.class, NavTree.Item.page("Giai trình công", WorkExplanationPage.class));
//        map.put(LeaveRequestPage.class, NavTree.Item.page("Xin nghỉ phép", LeaveRequestPage.class));
        map.put(SalaryReportPage.class, NavTree.Item.page("Phiếu báo lương", SalaryReportPage.class));

        // Thông tin cá nhân
        map.put(ChangePasswordPage.class, NavTree.Item.page("Đổi mật khẩu", ChangePasswordPage.class));
        map.put(UpdatePersonalInfoPage.class, NavTree.Item.page("Thay đổi thông tin cá nhân", UpdatePersonalInfoPage.class));

        return map;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void navigate(Class<? extends Page> page) {
        selectedPage.set(Objects.requireNonNull(page));
        currentSubLayer.set(PAGE);
    }

    public void showSourceCode() {
        currentSubLayer.set(SOURCE_CODE);
    }

    public void hideSourceCode() {
        currentSubLayer.set(PAGE);
    }
}
