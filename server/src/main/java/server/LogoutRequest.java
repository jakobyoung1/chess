package server;

record LogoutRequest(
        String username,
        String password,
        String authToken){
}