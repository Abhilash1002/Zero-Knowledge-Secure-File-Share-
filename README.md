# My Project Name

This project consists of a Maven backend with SQL database integration and a React frontend built with Vite.

## Backend Setup

### Prerequisites

- Java JDK 11 or higher
- Maven
- MySQL (or your preferred SQL database)

### Steps

1. Clone the repository:

   ```
   git clone https://github.com/yourusername/your-repo-name.git
   cd your-repo-name/backend
   ```

2. Configure the database:

   - Create a new database in MySQL
   - Update the `application.properties` file in `scs-backend/src/main/resources` with your database credentials:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. Build the project:
   ```
   mvn clean install
   ```

## Frontend Setup

### Prerequisites

- Node.js (version 14 or higher)
- npm (usually comes with Node.js)

### Steps

1. Navigate to the frontend directory:

   ```
   cd ../frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

## Running the Application

### Backend

1. Start the backend server:
   ```
   cd backend
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080` (or your configured port).

### Frontend

1. Start the Vite development server:
   ```
   cd frontend
   npm run dev
   ```
   The frontend will start on `http://localhost:5173` (or another available port).

## UI Interaction

### Prerequisites

- Have some files saved on your local which you want to upload.
- Ensure you save your private key upon first signUp

### Steps

1. Navigate to the [frontend page](http://localhost:5173/):
2. Choose Signup and add your credentials
3. Login into your account, click on 'generate Keys' if it's a first time sign-up
4. Now your account is set to go.
5. upload files, and you are good to play around with the UI.

## Additional Information

Read more about:

1. [vite.js](https://vitejs.dev/guide/)
2. [SpringBoot](https://spring.io/projects/spring-boot)
3. [MySql](https://dev.mysql.com/doc/)
