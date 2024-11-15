package atlantafx.sampler.base.entity.common;

public class Products {
    private int id;
    private String imageLink;
    private String name;
    private double price;
    private int categoryId;

    public Products(int id, String imageLink, String name, double price, int categoryId) {
        this.id = id;
        this.imageLink = imageLink;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
    }

    public Products(String imageLink, String name, double price, int categoryId) {
        this.imageLink = imageLink;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
    }

    public Products() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
