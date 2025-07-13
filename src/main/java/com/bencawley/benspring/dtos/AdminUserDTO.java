package com.bencawley.benspring.dtos;

import com.bencawley.benspring.utilities.UserRole;


/* Note: So I created this so that Admins could get more data for user accounts then the individual users...
    however turns out that the user still needs this data :P

    But in saying that im not going to rip this out. Im leaving it in for scalability incase I did want to attach admin only
    information for some reason like UserID. And it gives me a reason to create the /ME endpoint, so I get to explore and learn
    more things.

    Yes I am well aware that leaving things like this in a production project is not scalability, its legacy code awaiting
    refactoring in techdebt but remember this is not a Production project it's a learning project so its more valuable to be
    left in.

* */

public class AdminUserDTO {
    private String username;
    private String email;
    private UserRole role;

    public AdminUserDTO(String username, String email, UserRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and setters

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}
}
