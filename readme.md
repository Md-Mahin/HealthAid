# JavaFX Project (SDK 25)

This project is a Java application built using **JavaFX SDK 25**.  
Follow the instructions below to properly set up, build, and run the project using IntelliJ IDEA.

---

## 🛠 Requirements

Make sure you have the following installed:

- Java JDK (17 or higher recommended)
- IntelliJ IDEA
- JavaFX SDK 25

---

## Installation & Setup Guide

### 1. Download JavaFX SDK

1. Download JavaFX SDK 25 from the official Gluon website  
2. Extract the downloaded file to a directory on your system  

Example (Windows):

---

### 2. Open the Project in IntelliJ

1. Open IntelliJ IDEA  
2. Click **Open**  
3. Select the project folder  

---

### 3. Configure JavaFX Library

1. Go to: File → Project Structure → Libraries
2. Click **+ → Java**
3. Navigate to the JavaFX SDK folder and select:D:\javafx-sdk-25.0.2\lib or your javafx lib folder
4. Click **OK → Apply**

---

### 4. Add VM Options

1. Go to:Run → Edit Configurations
2. In the **VM options** field, add:
--module-path "javafxPath" --add-modules javafx.controls,javafx.fxml

---

## Build Executable JAR (Artifact)

### Step 1: Create Artifact

1. Go to: File → Project Structure → Artifacts
2. Click:→ JAR → From modules with dependencies
3. Select your **Main Class**
4. Choose:Extract to the target JAR
5. Click **OK → Apply**

---

### Step 2: Build the Artifact

1. Go to: Build → Build Artifacts
2. Select your artifact  
3. Click **Build**

The generated `.jar` file will be located in:
out/artifacts/

---

##  Run the Application

To run the generated `.jar` file, use the following command:

```bash
java --module-path "D:\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml -jar your-app.jar