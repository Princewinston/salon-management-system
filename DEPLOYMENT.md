# Deployment Guide: Salon Management System with Docker

## Prerequisites
- Docker Installed (Desktop or Engine)
- Java 17 (Optional if just running, but needed to build the JAR)

## Step 1: Build the JAR File
Before building the Docker image, you must package the Spring Boot application into a JAR file.
Open your terminal in the project root (`p:\salon_management`) and run:

```sh
./mvnw clean package -DskipTests
```
*(On Windows Command Prompt, just run `mvnw clean package -DskipTests`)*

This will create `salon_management-0.0.1-SNAPSHOT.jar` in the `target/` folder.

## Step 2: Run with Docker Compose
We have set up a `docker-compose.yml` file that creates two containers:
1. **mysqldb**: The MySQL database (Port 3307 on host -> 3306 in container).
2. **app**: The Salon Management Application (Port 8080).

Run the following command:

```sh
docker-compose up --build
```

- `--build` forces a rebuild of the image (useful if you changed code).
- Add `-d` to run in detached mode (background): `docker-compose up -d --build`.

## Step 3: Access the Application
Once the containers are running, access the app at:
**[http://localhost:8080](http://localhost:8080)**

## Notes
- **Database**: The containerized app connects to the containerized MySQL named `mysqldb`.
- **Data Persistence**: Database data is stored in a docker volume `mysql_data` so it persists across restarts.
- **Port Conflicts**: If port 8080 is in use, change the mapping in `docker-compose.yml` (e.g., `"8081:8080"`).
- **Stopping**: Run `docker-compose down` to stop and remove containers. Use `docker-compose down -v` to also remove the database volume (WARNING: this deletes all data).

---

# Deploying to the Internet (Production)

To deploy this application to the cloud so anyone can access it, follow these steps using a **Cloud VPS** (Virtual Private Server) provider like **AWS EC2**, **DigitalOcean Droplet**, or **Linode**.

## Phase 1: Prepare the Server
1.  **Create a Server**: Launch a Linux server (Ubuntu 22.04 LTS is recommended).
2.  **Install Docker**: SSH into your server and install Docker & Docker Compose.
    ```sh
    # Update packages
    sudo apt update
    # Install Docker
    sudo apt install docker.io docker-compose -y
    # Start Docker
    sudo systemctl start docker
    sudo systemctl enable docker
    ```

## Phase 2: Transfer Files
You need to move your project files to the server. The easiest way is to build the JAR locally and copy it, OR copy the source and build on the server. Here is the **Copy JAR** method:

1.  **Build Locally**: Run `./mvnw clean package -DskipTests` on your machine.
2.  **Copy Files**: Use `scp` (Secure Copy) to upload the essential files to your server (replace `user@your-server-ip` with your actual server details):
    ```sh
    scp target/salon_management-0.0.1-SNAPSHOT.jar user@your-server-ip:~/app.jar
    scp Dockerfile user@your-server-ip:~/Dockerfile
    scp docker-compose.yml user@your-server-ip:~/docker-compose.yml
    ```
    *(Note: You might want to create a folder on the server first, e.g., `mkdir salon_app` and copy into that.)*

## Phase 3: Run on Server
1.  **SSH into your server**:
    ```sh
    ssh user@your-server-ip
    ```
2.  **Modify Dockerfile** (Optional but recommended):
    Since we uploaded `app.jar` directly to `~`, update `Dockerfile` to copy from the current directory instead of `target/`.
    *Change `COPY target/salon_management...` to `COPY app.jar app.jar` if needed, or just ensure the paths match.*
    
3.  **Start the Application**:
    ```sh
    sudo docker-compose up -d --build
    ```

4.  **Open Ports**:
    Ensure your cloud provider's firewall (Security Group in AWS) allows **Port 8080** (and 22 for SSH).

## Phase 4: Access
Open your browser and go to:
`http://your-server-ip:8080`

You are now live!
