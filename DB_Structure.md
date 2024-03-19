# Database structure
## Description
This is the structure of the database.
This structure only contains the basic elements needed for the main functions of the app.
It is expected to evolve in the development of the app.
- USER: a user of the app, metadata reference the attributes defined by https://supabase.com/docs/guides/auth/auth-user-management
- USER_PROFILE: information related to a user
- EVENT: an event created in the app, created by a user; the user has the choice to create the event for an association (host relation) or for himself
- TAG: a tag that can be linked to the user's preferences and the event's tags list
- ASSOCIATION: an association that has some committee members and followers, the committee members can create events for the association

## Diagram
```mermaid
erDiagram
    USER_PROFILE {
        string userId PK,FK
        string name UK
    }
    USER {
        string userId PK
        string metadata
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
        string name UK
    }

ASSOCIATION {
    string associationId PK
    string name UK
    string description

}
    USER }o--|| EVENT : organizer
    USER }o--o{ EVENT: participant
    USER }o--o{ TAG: interested
    USER ||--|| USER_PROFILE: profile
    EVENT }o--o{ TAG: tags
    TAG }o--o{ TAG: subtag
    ASSOCIATION }|--o{ USER: committee_member
    ASSOCIATION }o--o{ USER: follower
    ASSOCIATION }o--o{ EVENT: host
```
For mermaid syntax, see: https://mermaid.js.org/syntax/entityRelationshipDiagram.html
