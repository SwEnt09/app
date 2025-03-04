# Association Integration
## Description
This file tries to define how the associations will be handled in our application.
Firstly, let's see some user stories linked to associations and then how we could implement these functionalities :
## User Stories
- As a user interested in philosophy, I want to be able to find related associations so that I don't need to put too much effort into searching.
- As a user potentially interested in joining JDRPoly, I want to be able to get enough information on this association so that I know if it interests me or not.
- As a curious user, I want to be able to discover new associations that I never thought of so that I can start a new activity.
- As a user, I want to be able to follow an association so that I can easily see the latest event feeds and events related to it.
- As a user in charge of communication in my association, I want to be able to share latest event feeds and stories so that people can see what's happening in my association.
- As a user in charge of communication in my association, I want to be able to create events and certify them with my association so that other users can see that my event is from my association.
## Implementation
### Dataclass Association
We already have a dataclass called Association. Currently, it has the following attributes : an "associationId", a "name" and a "description". We can add to this :
- "relatedTags: Set<Tag>", set of all tags related to the association
- "associationURL : URL", the most important URL of the association (website, facebook, instagram, ...)
### AssociationsScreen
In the hamburger menu, we can add a route to go to an AssociationsScreen where we can find everything related to them. 
The first thing a user sees is the active events of each association she/he follows. Reuse the ListDrawer. 
On top of this page, we have two buttons (or better MD component) : one with "Followed Associations" which leads to a list of followed associations by a user and one with "My Associations" which leads to a list of associations to which the user belongs. 
At the bottom right, there is a search button (like the one in HomeScreen) which allows the user to search an association. 
### FollowedAssociationsPage
Already designed by Yoan (just needs little update), it consists of the list of all followed associations under the form of a list. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### CommitteeAssociationsPage
Already designed by Yoan (just needs little update), it consists of the list of all associations to which the user belongs. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### SearchMenuAssociations
When the search button is clicked, it opens a bottom sheet with exactly the same functionality as Discover, but without the filters (which doesn't make sense for searching associations). A list with all associations is displayed in the background and updated according to the bottom sheet. If an item of the list is clicked, it leads the user to the specific AssociationPage.
### AssociationPage
On top, we have the choice of two pages : a "Description" default page and a "Event feed" page.
#### DescriptionPage
This page consists of the title and description of the association. On top right, there is a button to follow/unfollow the association. If we scroll down, we can see a link to the association website, then a link to the instagram, and an e-mail to contact if the user is interested in joining the association (or for whatever question). If the user belongs to the moderators, she/he has an additional "Edit" button to modify the displayed information.
#### ActualityPage
This page shows the history of all events of the association in chronological order. Reuse the ListDrawer already implemented in HomeScreen, filtered to display only events of the association.