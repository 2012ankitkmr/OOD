package MeetingScheduler;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

enum UserRole{
    ORGANISER, PARTICIPANT
}
class User{
    private Long userId;
    private String emailId;
    private String name;

    public User(String emailId, String name) {
        this.userId = new Random().nextLong();
        this.emailId = emailId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
class EventProposedTime {
    String userEmailId;
    Date date;
    Time startTime;
    Time endTime;

    public EventProposedTime(String userEmailId, Date date, Time startTime, Time endTime) {
        this.userEmailId = userEmailId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
class EventDetailModel{
    private String agenda;
    private String location;
    private List<EventProposedTime> proposedTimes;

    public EventDetailModel(String agenda) {
        this.agenda = agenda;
    }

    public EventDetailModel(String agenda, String location) {
        this.agenda = agenda;
        this.location = location;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<EventProposedTime> getProposedTimes() {
        return proposedTimes;
    }

    public void setProposedTimes(List<EventProposedTime> proposedTimes) {
        this.proposedTimes = proposedTimes;
    }
}
class EventModel{
    private Long eventId;
    private Long userId;
    private String eventName;
    private List<String> participants;
    private Date eventDate;
    private Time startTime;
    private Time endTime;
    private EventDetailModel eventDetail;

    public EventModel(Long userId, String eventName, List<String> participants,
                      Date eventDate, Time startTime, Time endTime,
                      EventDetailModel eventDetail) {
        this.eventId = new Random().nextLong();
        this.userId = userId;
        this.eventName = eventName;
        this.participants = participants;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventDetail = eventDetail;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public EventDetailModel getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(EventDetailModel eventDetail) {
        this.eventDetail = eventDetail;
    }
}
enum InviteResponseType{
    YES, NO, MAYBE, PROPOSED_NEW_TIME
}
class InviteModel<T> {
    Long inviteId;
    Long eventId;
    Long participantId;
    boolean inviteSent;
    InviteResponseType responseType;
    T responseData;

    public InviteModel(Long eventId, Long participantId, boolean inviteSent) {
        this.eventId = eventId;
        this.participantId = participantId;
        this.inviteSent = inviteSent;
    }

    public InviteModel(Long eventId, Long participantId, boolean inviteSent,
                       InviteResponseType responseType, T responseData) {
        this.inviteId = new Random().nextLong();
        this.eventId = eventId;
        this.participantId = participantId;
        this.inviteSent = inviteSent;
        this.responseType = responseType;
        this.responseData = responseData;
    }

    public Long getInviteId() {
        return inviteId;
    }

    public void setInviteId(Long inviteId) {
        this.inviteId = inviteId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public boolean isInviteSent() {
        return inviteSent;
    }

    public void setInviteSent(boolean inviteSent) {
        this.inviteSent = inviteSent;
    }

    public InviteResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(InviteResponseType responseType) {
        this.responseType = responseType;
    }

    public T getResponseData() {
        return responseData;
    }

    public void setResponseData(T responseData) {
        this.responseData = responseData;
    }
}
class Database{
    static private List<EventModel> events;
    static private List<InviteModel> invites;
    static private List<User> users;
    static  {
        users = new ArrayList(){{
            add(new User("email1@gmail.com", "user1"));
            add(new User("email2@gmail.com", "user2"));
            add(new User("email3@gmail.com", "user3"));
        }};
        invites = new ArrayList();
        events = new ArrayList();
    }
    static User getUser(Long userId){
        return users.stream().filter(user -> user.getUserId().equals(userId)).findAny().orElse(null);
    }
    static User getUserByEmail(String emailId){
        return users.stream().filter(user -> user.getEmailId().equals(emailId)).findAny().orElse(null);
    }
    static void addEvent(EventModel event){
        events.add(event);
    }
    static void addInvite(InviteModel invite){
        invites.add(invite);
    }
    static InviteModel getInvite(Long inviteId){
        return invites.stream().filter(invite -> invite.getInviteId().equals(inviteId)).findAny().orElse(null);
    }
    static EventModel getEvent(Long eventId){
        return events.stream().filter(event -> event.getEventId().equals(eventId)).findAny().orElse(null);
    }
    static List<EventModel> getAllEventsOfUser(Long userId, Date date){
        List<Long> allParticipatedEventIds = invites.stream().filter(invite -> invite.participantId.equals(userId)).map(InviteModel::getEventId).collect(Collectors.toList());
        List<EventModel> allEvents = events.stream().filter(event ->
                    (event.getUserId().equals(userId) || allParticipatedEventIds.contains(event.getEventId())) &&
                    (event.getEventDate().compareTo(date) == 0)
        ).collect(Collectors.toList());
        return allEvents;
    }
}
class EventRequest{
    Long userId;
    String eventName;
    String agenda;
    Date eventDate;
    Time startTime;
    Time endTime;
    String eventLocation;
    List<String> participantList;

    public EventRequest(Long userId, String eventName, String agenda, Date eventDate,
                        Time startTime, Time endTime, String eventLocation,
                        List<String> participantList) {
        this.userId = userId;
        this.eventName = eventName;
        this.agenda = agenda;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventLocation = eventLocation;
        this.participantList = participantList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<String> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<String> participantList) {
        this.participantList = participantList;
    }

}
class EventDetailResponse{
    Long eventId;
    UserRole role;
    Time startTime;
    Time endTime;
    List<EventProposedTime> proposedTimeList;
    String eventName;
    String agenda;

    public EventDetailResponse(Long eventId, UserRole role, Time startTime, Time endTime,
                               List<EventProposedTime> proposedTimeList, String eventName, String agenda) {
        this.eventId = eventId;
        this.role = role;
        this.startTime = startTime;
        this.endTime = endTime;
        this.proposedTimeList = proposedTimeList;
        this.eventName = eventName;
        this.agenda = agenda;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public List<EventProposedTime> getProposedTimeList() {
        return proposedTimeList;
    }

    public void setProposedTimeList(List<EventProposedTime> proposedTimeList) {
        this.proposedTimeList = proposedTimeList;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
}
enum EventUpdateActionType{
    UPDATE_EVENT_NAME, UPDATE_EVENT_AGENDA, UPDATE_EVENT_TIME, ADD_PARTICIPANTS
}
interface EventService{
    void createEvent(EventRequest request);
    List<EventDetailResponse> getAllEvents(Long userId, Date eventDate);
    void updateEvent(EventUpdateActionType actionType, Long eventId, EventRequest request);
    boolean checkAvailability(Long userId, Date date, Time startTime, Time endTime);
    <T> void respondInvite(Long inviteId, InviteResponseType responseType, T responseDate);
}

class NotificationService{
    static void sendEmail(String emailId, String emailBody) {
        System.out.printf("Sending email to the emailId %s, email body %s", emailId, emailBody);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class EventServiceImpl implements EventService{
    @Override
    public void createEvent(EventRequest request) {
        EventModel event = new EventModel(
                request.getUserId(),
                request.getEventName(),
                request.getParticipantList(),
                request.getEventDate(),
                request.getStartTime(),
                request.getEndTime(),
                new EventDetailModel(request.getAgenda())
        );
        Database.addEvent(event);
        for(String emailId : request.getParticipantList()){
            NotificationService.sendEmail(emailId, String.format("Sending invite for the event %s", event.getEventId()));
            User user = Database.getUserByEmail(emailId);
            InviteModel inviteModel = new InviteModel(event.getEventId(), user.getUserId(), true);
            Database.addInvite(inviteModel);
        }
    }

    @Override
    public List<EventDetailResponse> getAllEvents(Long userId, Date eventDate) {
        List<EventModel> eventsFromDB = Database.getAllEventsOfUser(userId, eventDate);
        if(eventsFromDB == null) return Collections.emptyList();
        List<EventDetailResponse> events = new ArrayList<>();
        eventsFromDB.forEach(event -> {
            events.add(new EventDetailResponse(
                    event.getEventId(),
                    event.getUserId().equals(userId) ? UserRole.ORGANISER : UserRole.PARTICIPANT,
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getEventDetail().getProposedTimes(),
                    event.getEventName(),
                    event.getEventDetail().getAgenda()
            ));
        });
        return events;
    }

    @Override
    public void updateEvent(EventUpdateActionType actionType, Long eventId, EventRequest request) {
        EventModel event = Database.getEvent(eventId);
        if(EventUpdateActionType.UPDATE_EVENT_NAME.equals(actionType)){
            event.setEventName(request.getEventName());

        } else if(EventUpdateActionType.UPDATE_EVENT_AGENDA.equals(actionType)){
            event.getEventDetail().setAgenda(request.getAgenda());

        } else if(EventUpdateActionType.UPDATE_EVENT_TIME.equals(actionType)){
            event.setEventDate(request.getEventDate());
            event.setStartTime(request.getStartTime());
            event.setEndTime(request.getEndTime());

        } else if(EventUpdateActionType.ADD_PARTICIPANTS.equals(actionType)){
            List<String> participants = event.getParticipants();
            List<String> newParticipants = new ArrayList<>();

            for(String emailId : request.getParticipantList()){
                if(participants != null && participants.contains(emailId)){ continue; }
                newParticipants.add(emailId);
                NotificationService.sendEmail(emailId, String.format("Sending invite for the event %s", event.getEventId()));
                User user = Database.getUserByEmail(emailId);
                InviteModel inviteModel = new InviteModel(event.getEventId(), user.getUserId(), true);
                Database.addInvite(inviteModel);
            }
            if(event.getParticipants() == null){
                event.setParticipants(new ArrayList<>());
            }
            event.getParticipants().addAll(newParticipants);
        }
    }

    @Override
    public boolean checkAvailability(Long userId, Date date, Time startTime, Time endTime) {
        List<EventModel> allEvents = Database.getAllEventsOfUser(userId, date);
        boolean isOverlap = false;
        for (EventModel event: allEvents) {
            if((event.getStartTime().before(startTime) && event.getEndTime().before(startTime)) ||
                    (event.getStartTime().after(startTime) && event.getStartTime().before(endTime))){
                isOverlap = true;
                break;
            }
        }
        return !isOverlap;
    }

    @Override
    public <T> void respondInvite(Long inviteId, InviteResponseType responseType, T responseDate) {
        InviteModel invite = Database.getInvite(inviteId);
        invite.setResponseType(responseType);
        invite.setResponseData(responseDate);

        if(InviteResponseType.PROPOSED_NEW_TIME.equals(responseType)){
            EventModel event = Database.getEvent(invite.getEventId());
            EventProposedTime proposedNewTime = (EventProposedTime) responseDate;
            User user = Database.getUser(invite.getParticipantId());
            ((EventProposedTime) responseDate).setUserEmailId(user.getEmailId());
            if(event.getEventDetail().getProposedTimes() == null){
                event.getEventDetail().setProposedTimes(new ArrayList<>());
            }
            event.getEventDetail().getProposedTimes().add(proposedNewTime);
        }
        System.out.println("Invite response saved successfully!!");
    }
}

public class Main {

}
