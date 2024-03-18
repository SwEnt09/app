# Database structure
## Description
This is the structure of the database.
This structure only contains the basic elements needed for the main functions of the app.
It is expected to evolve in the development of the app.
- USER: a user of the app
- EVENT: an event created in the app, created by a user
- TAG: a tag that can be linked to the user's preferences and the event's tags list
- ASSOCIATION: an association that has some committee members and followers, the committee members can create events for the association
## Diagram
```mermaid
erDiagram
    USER {
        string userId PK
        string name
        location location
    }
    EVENT {
        string eventId PK
        string organizerId FK
        string title
        string description
        location location
        date start_date
        date end_date
    }
    TAG {
        string tagId PK
        string name
    }

ASSOCIATION {
    string associationId PK
    string name
    string description

}
    USER }o--|| EVENT : organizer
    USER }o--o{ EVENT: participant
    USER }o--o{ TAG: interested
    EVENT }o--o{ TAG: tags
    TAG }o--o{ TAG: subtag
    ASSOCIATION }|--o{ USER: committee_member
    ASSOCIATION }o--o{ USER: follower
```
For mermaid syntax, see: https://mermaid.js.org/syntax/entityRelationshipDiagram.html
