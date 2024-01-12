import org.rumel.User;
import org.rumel.userGrpc;
import io.grpc.stub.StreamObserver;

import javax.xml.transform.Result;
import java.sql.*;

public class UserService extends userGrpc.userImplBase {
    //database
    String url = "jdbc:mysql://localhost/profilemanagement";
    String name = "root";
    String pass = "";

    @Override
    public void register(User.RegistrationRequest request, StreamObserver<User.RegistrationResponse> responseObserver) {
        String regUsername =request.getUsername();
        String regPassword = passwordHasher(request.getPassword());

        User.RegistrationResponse.Builder regResponse = User.RegistrationResponse.newBuilder();
        try (Connection connection = DriverManager.getConnection(url, name, pass)){
            String query = "SELECT username FROM user_authentication WHERE username=?";
            PreparedStatement pStatement =connection.prepareStatement(query);
            pStatement.setString(1, regUsername);
            ResultSet userResult = pStatement.executeQuery();

            if(!userResult.next()) {
                String regQuery = "INSERT into user_authentication(username, hashed_pass, created_at) VALUES(?, ?, ?)";
                PreparedStatement preStatement = connection.prepareStatement(regQuery);
                preStatement.setString(1, regUsername);
                preStatement.setString(2, regPassword);
                preStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                int done = preStatement.executeUpdate();
                if(done == 1) {
                    regResponse.setResponseCode(200).setMessage(regUsername + " was registered");
                }
                else {
                    regResponse.setResponseCode(400).setMessage("Registration Failed. Try again.");
                }
            }
            else {
                regResponse.setResponseCode(400).setMessage("Username unavailable. Try again");
            }
            responseObserver.onNext(regResponse.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            regResponse.setResponseCode(500).setMessage("Server Error");
            responseObserver.onNext(regResponse.build());
            responseObserver.onCompleted();
        }
    }

    private String passwordHasher(String password) {
        char[] string = password.toCharArray();
        String hash="";
        for (int i=0;i< password.length();i+=2){
            hash += (char)(string[i]+(i%30));
        }
        for(int i=1;i<password.length();i+=2){
            hash+=(char)(string[i]+(i%35));
        }
        return hash;
    }

    @Override
    public void login(User.LoginRequest request, StreamObserver<User.Response> responseObserver) {
        String username =request.getUsername();
        String password = passwordHasher(request.getPassword());

        User.Response.Builder response  = User.Response.newBuilder();

        try (Connection connection = DriverManager.getConnection(url, name, pass)) {
            String loginQuery = "SELECT hashed_pass FROM user_authentication WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String userpass = resultSet.getString("hashed_pass");
                if (password.equals(userpass)) {
                    response.setResponseCode(200).setMessage("OK! Login Successful");
                } else {
                    response.setResponseCode(400).setMessage("Bad Request !!!");
                }
            } else {
                response.setResponseCode(400).setMessage("Bad Request !!!");
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            response.setResponseCode(500).setMessage("Server Error");
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createProfile(User.ProfileRequest request, StreamObserver<User.ProfileResponse> responseObserver) {
        User.ProfileResponse.Builder profileResponse = User.ProfileResponse.newBuilder();

        try (Connection connection = DriverManager.getConnection(url, name, pass)) {
            String createProfileQuery = "INSERT INTO user_profile(username, fullname, email, created_at, updated_at) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(createProfileQuery);
            preparedStatement.setString(1, request.getUsername());
            preparedStatement.setString(2, request.getFullName());
            preparedStatement.setString(3, request.getEmail());
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            int insertedRows = preparedStatement.executeUpdate();

            if (insertedRows == 1) {
                profileResponse.setResponseCode(201).setMessage("Profile created successfully");
            } else {
                profileResponse.setResponseCode(400).setMessage("Failed to create profile");
            }

            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            profileResponse.setResponseCode(500).setMessage("Server Error");
            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateProfile(User.UpdateProfileRequest request, StreamObserver<User.ProfileResponse> responseObserver) {
        User.ProfileResponse.Builder profileResponse = User.ProfileResponse.newBuilder();

        try (Connection connection = DriverManager.getConnection(url, name, pass)) {
            String updateProfileQuery = "UPDATE user_profile SET fullname = ?, email = ?, updated_at = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateProfileQuery);
            preparedStatement.setString(1, request.getFullName());
            preparedStatement.setString(2, request.getEmail());
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(4, request.getUsername());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 1) {
                profileResponse.setResponseCode(200).setMessage("Profile updated successfully");
            } else {
                profileResponse.setResponseCode(400).setMessage("Failed to update profile");
            }

            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            profileResponse.setResponseCode(500).setMessage("Server Error");
            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void showUserProfile(User.ShowUserProfileRequest request, StreamObserver<User.ShowUserProfileResponse> responseObserver) {
        User.ShowUserProfileResponse.Builder profileResponse = User.ShowUserProfileResponse.newBuilder();
        String username = request.getUsername();

        try (Connection connection = DriverManager.getConnection(url, name, pass)) {
            // Execute a database query to retrieve the user's profile
            String profileQuery = "SELECT username, fullname, email FROM user_profile WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(profileQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String fullName = resultSet.getString("fullname");
                String email = resultSet.getString("email");

                profileResponse.setResponseCode(200).setUsername(username).setFullName(fullName).setEmail(email);
            } else {
                profileResponse.setResponseCode(404) ;
            }

            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            profileResponse.setResponseCode(500) ;
            responseObserver.onNext(profileResponse.build());
            responseObserver.onCompleted();
        }
    }
}
