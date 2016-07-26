# Gameserver 

This is going to be a tool for assisting those who run tabletop RPGs. It will allow game masters to create campaign and game information and then to use it while running a game. Players of the game will also have tools available to assist during game play. 

The app uses mongoDB, Java, Spring MVC, Spring Security, Thymeleaf, JQuery, and Bootstrap. 

It will manage users permissions and access to pages and methods via Spring Security.

# Why did I do some of the things I have done?
1Q. Why are only Admins (users with ADMIN role) allowed to import and export campaign data?

1A. There are security issues involved in writing the export file to the server where the app is running. If the app is running on a remote machine it is reasonable to assume that only someone with Admin access would be able to retrieve the export file from that machine. I might add "export and email" in the future so that users with GAMEMASTER roles could export but import will still only be allowable by Admins. The reason for that is that importing also imports users who are associated with the given campaign as well. Allowing gamemasters to import would be to take away who has control over users of the application from the admin and give it to users. This is not a good idea.
