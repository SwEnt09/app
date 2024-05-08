# Association Integration
## Desciption
This file try to define how the associations will be handled in our application.
Firstly, let see some user stories linked to associations. Then, how we can implement those functionalities :
## User Stories
- As a user interested in philosophy, I want to be able to find related associations so that I don't have to make too much effort.
- As a user potentially interested in joining JDRPoly, I want to be able to get enough information on this association so that I know if it interests me or not.
- As a curious user, I want to be able to discover new associations that I never thought of so that I can start a new activity.
- As a user, I want to be able to follow an association so that I can easily see news and events related to it.
- As a user in charge of communication in my association, I want to be able to share latest news so that people can see what's happening in my association.
- As a user in charge of communication in my association, I want to be able to create events and certified them with my association so that other users can see that my event is from my association.
## Implementation
### Dataclass Association
We already have a dataclass called Association. Currently, it has the following attributes : an "associationId", a "name" and a "description". We can add to this :
- "relatedTags: Set<Tag>", the set of all tags related to the association
- "moderators: Set<User>", the set of people that have the right to publish actuality, modify association description, add members and create events in the name of the association
- TO DISCUSS : "members: Set<User>", list of all approved members of the association
- "e-mail : String", a reference to the e-mail of the association if users want to contact them
- "website: String? = "" ", a reference to the website of the association for those who have one
- "instagram: String? = "" ", a reference to the instagram of the association for those who have one
### AssociationsScreen
In the hamburger menu, we can add a route to go to an AssociationsScreen where we can find everything related to associations. 
The first thing a user see is the recent actuality of each associations she/he follows, like a feed (For simplicity, instead of Actuality, we can firstly just show all events created by the association). 
On top of this page, we have two buttons : one with "Followed Associations" which leads to a list of followed associations and one with "My Associations" which leads to a list of associations in which the user belong. 
At the bottom right, there is a search button (like the one in HomeScreen) which allows the user to search an association. 
### FollowedAssociationsPage
Already designed by Yoan (just needs little update), it consists of the list of all followed associations under the form of a list. Each item of the list have an unsubscribe button. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### ComiteeAssociationsPage
Already designed by Yoan (just needs little update), it consists of the list of all associations in which the user belong. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### SearchMenuAssociations
When the search button is clicked, it opens a bottom sheet with exactly the same functionnality as Discover, but without the filters (which doesn't make sense to search associations). A list with all associations is displayed in the background and updated according to the bottom sheet. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### AssociationPage
On top, we have the choice of two pages : a "Description" default page and a "Actuality" page.
#### DescriptionPage
This page consists of the title and description of the association. On top right, there is a button to follow/unfollow the association. If we scroll down, we can see a link to the association website, then a link to the instagram, and an e-mail to contact if the user is interested in joining the association (or for whatever question). If the user belongs to the moderators, she/he has an additional "Edit" button to modify the displayed information.
#### ActualityPage
This page shows the history of all posts of the association (like a feed). If the user belongs to the moderators, she/he can post new actualities in the associtations feed for the followers.(For simplicity, instead of Actuality, we can firstly just show all events created by the association)