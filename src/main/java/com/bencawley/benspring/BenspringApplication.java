package com.bencawley.benspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// todo start writting the tests before you regret it and brake things.

/*
	So this app was built so that I could learn more about full stack development.
	It's nothing complex or creative.

	The basics of how this works is.
	We have security (session tokens), A database to store information,
	endpoints to talk to the database, and some data to store in the database.
	It is also has multiple user support.

	The data that is stored is reminder checklists.
	So each user has a collection of checklists.
	each checklist has a collection of items.
	Each item contains a title, due date, description and checked state.

	A user can view, create, modify and delete the items in a list or a list itself using API calls.
	That is how this app works from a high level perspective

	However, from a more detailed perspective.
	Each item holds not only its information stated above but also holds an ID and the ID of what list it belongs to.
	Each list holds its own ID and the ID of what User it belongs to.
	and by doing this we can fetch the items by having the server query the lists that have the userID and then the items that hold that list ID
	However with this pattern alone its posable to fetch data from other uses by simpley changing the userID.
	So to fix that the User ID is never sent to the client. instead.
	When a user creates an account the User ID is generated on the server and sends back a session token that is paired with the user ID.
	when any requests are made to access data the server looks up the UserID from the SessionID it is paired with and uses that to fetch the data.
	Therefore, denying the client "root" access. By root, I mean if we imagination the data path being userID/listID/itemID where userID is the root
	of the path, the client is not able to change the value of the root which would allow them to access to the other data.

	A SessionToken is also required to access any part of the api apart from the authentication endpoints.
	And an admin SessionToken is required to access administration requests.

	The reason why I have it so that a child keeps track of its parent instead of a parent keeping track of its child is that its easer for requests.
	If a child is created or deleted only one path is needed to be accessed, the path to the child. However, if the parent had to keep track of the child.
	then not only do we have to hit the endpoint to create or delete the child, but we would also have to hit a put endpoint for the parent to modify itself.

 */

@SpringBootApplication(
		exclude = { SecurityAutoConfiguration.class }
)
public class BenspringApplication {
	public static void main(String[] args) {
		SpringApplication.run(BenspringApplication.class, args);
	}
}

