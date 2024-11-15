package atlantafx.sampler.cashier.layout;

import atlantafx.sampler.cashier.event.DefaultEventBus;
import atlantafx.sampler.cashier.event.NavEvent;
import atlantafx.sampler.cashier.page.Page;
import atlantafx.sampler.cashier.page.components.EditTableList;
import atlantafx.sampler.cashier.page.components.ListProductPage;
import atlantafx.sampler.cashier.page.components.TableListPage;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static atlantafx.sampler.cashier.layout.MainModel.SubLayer.PAGE;
import static atlantafx.sampler.cashier.layout.MainModel.SubLayer.SOURCE_CODE;

public class MainModel {

    public static final Class<? extends Page> DEFAULT_PAGE = TableListPage.class;

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
    private final ReadOnlyObjectWrapper<NavTree.Item> navTree = new ReadOnlyObjectWrapper<>(
        createTree());

    public ReadOnlyObjectProperty<NavTree.Item> navTreeProperty() {
        return navTree.getReadOnlyProperty();
    }

    private NavTree.Item createTree() {
        var root = NavTree.Item.root();

        // Grouped pages
        var groupedPages = NavTree.Item.group("Grouped Pages", new FontIcon(Material2OutlinedMZ.TABLET));
        groupedPages.getChildren().setAll(
                NAV_TREE.get(TableListPage.class),
                NAV_TREE.get(EditTableList.class)
        );

        // Non-grouped pages
      var listProductPage = NAV_TREE.get(ListProductPage.class);
if (listProductPage != null) {
    listProductPage.setGraphic(new FontIcon(Material2OutlinedMZ.SHOPPING_CART));
}

        root.getChildren().setAll(
                groupedPages,
                listProductPage
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
        map.put(EditTableList.class, NavTree.Item.page("Cài Đặt danh sách bàn", EditTableList.class));
        map.put(ListProductPage.class, NavTree.Item.page("Danh sách các đồ uống", ListProductPage.class));
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
