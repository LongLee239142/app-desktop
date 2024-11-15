# Project Setup and Run Guide

This guide provides instructions for setting up Maven, compiling, and running the project, as well as setting up the database.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure that JDK is installed. The project is compatible with JDK 8 or later.
- **Maven**: If you don’t have Maven installed, follow the instructions below to install it and add it to the PATH.

## Installing and Setting Up Maven

1. **Download Maven**:
    - Visit the [Apache Maven Download page](https://maven.apache.org/download.cgi).
    - Download the latest version of Maven.

2. **Extract Maven**:
    - Unzip the downloaded file to a directory on your computer, e.g., `C:\Program Files\Apache\maven`.

3. **Set Maven in System PATH** (Windows):
    - Open **Control Panel** > **System and Security** > **System** > **Advanced system settings**.
    - In the **System Properties** window, click on **Environment Variables**.
    - Under **System variables**, find the **Path** variable and click **Edit**.
    - Click **New** and add the path to the Maven `bin` directory, e.g., `C:\Program Files\Apache\maven\bin`.
    - Click **OK** to close each window.

4. **Verify Maven Installation**:
    - Open a command prompt and type:
      ```bash
      mvn -v
      ```
    - You should see Maven's version information if it's installed correctly.

## Building and Running the Project

After setting up Maven, navigate to the project’s root directory in your command prompt or terminal and follow these steps:

1. **Install Maven Dependencies**:
    - This command installs the project’s root `pom.xml` file without building the submodules:
      ```bash
      mvn install -N
      ```

2. **Build Specific Modules**:
    - Run the following commands to install each module individually:

        - **Install the `styles` module**:
          ```bash
          mvn install -pl styles
          ```

        - **Install the `base` module**:
          ```bash
          mvn install -pl base
          ```

3. **Run the Application**:
    - Once the modules are installed, you can run the application using the `sampler` module:
      ```bash
      mvn javafx:run -pl sampler
      ```

## Setting Up the Database

1. **Create the Database**:
    - Open your SQL database client and execute the following command to create a new database:
      ```sql
      CREATE DATABASE eproject2;
      ```

2. **Import the SQL File**:
    - Locate the SQL file in the `database` directory of the project.
    - In your SQL client, connect to the `eproject2` database and import the SQL file by executing:
      ```sql
      SOURCE path/to/your/sql/file.sql;
      ```
    - Replace `path/to/your/sql/file.sql` with the  SQL file in folder database.

The database should now be set up and ready for use by the application.

## Additional Information

- If you encounter any issues, verify that Java and Maven are correctly set up and that the database `eproject2` was created and imported properly.
- Refer to the `pom.xml` files in each module if you need to adjust any dependencies or plugin versions.

Enjoy your application!
