package in.queueless.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

public final class Domain {
    private Domain() {}
    public enum Role { CUSTOMER, OWNER }
    public enum QueueStatus { WAITING, SERVING, COMPLETED, CANCELLED, NO_SHOW }

    @Entity @Table(name="users")
    public static class User {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @Column(nullable=false, unique=true, length=254) public String email;
        @Column(nullable=false, length=100) public String name;
        @Column(columnDefinition="text") public String profilePhoto;
        @Column(nullable=false) public String passwordHash;
        @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) public Role role;
        @Column(nullable=false) public Instant createdAt = Instant.now();
        public Instant freeAccessUntil;
        public Instant subscriptionUntil;
        @Column(length=40) public String subscriptionPlan;
        protected User() {}
        public User(String name,String email,String passwordHash,Role role){this.name=name;this.email=email;this.passwordHash=passwordHash;this.role=role;}
    }

    @Entity @Table(name="organizations")
    public static class Organization {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @Column(nullable=false,length=140) public String name;
        @Column(nullable=false,length=140) public String category;
        @Column(nullable=false,length=180) public String address;
        @Column(nullable=false,length=80) public String city;
        @Column(length=1000) public String description;
        @Column(columnDefinition="text") public String profilePhoto;
        @Column(nullable=false) public boolean active=true;
        @Column(nullable=false) public Instant createdAt=Instant.now();
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public User owner;
        protected Organization() {}
        public Organization(String name,String category,String address,String city,String description,User owner){this.name=name;this.category=category;this.address=address;this.city=city;this.description=description;this.owner=owner;}
    }

    @Entity @Table(name="services")
    public static class ServiceItem {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public Organization organization;
        @Column(nullable=false,length=120) public String name;
        @Column(nullable=false) public int durationMinutes=15;
        @Column(nullable=false) public boolean active=true;
        protected ServiceItem() {}
        public ServiceItem(Organization organization,String name,int durationMinutes){this.organization=organization;this.name=name;this.durationMinutes=durationMinutes;}
    }

    @Entity @Table(name="staff")
    public static class StaffMember {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public Organization organization;
        @Column(nullable=false,length=100) public String name;
        @Column(length=120) public String title;
        @Column(nullable=false) public boolean active=true;
        protected StaffMember() {}
        public StaffMember(Organization organization,String name,String title){this.organization=organization;this.name=name;this.title=title;}
    }

    @Entity @Table(name="queue_entries", indexes={@Index(name="idx_queue_org_status",columnList="organization_id,status"),@Index(name="idx_queue_tracking",columnList="tracking_code",unique=true)})
    public static class QueueEntry {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public Organization organization;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public ServiceItem service;
        @ManyToOne(fetch=FetchType.EAGER) public StaffMember staff;
        @ManyToOne(fetch=FetchType.EAGER) public User customer;
        @Column(nullable=false,length=100) public String customerName;
        @Column(nullable=false,length=254) public String customerEmail;
        @Column(nullable=false,length=36,unique=true) public String trackingCode;
        @Column(nullable=false) public int queueNumber;
        @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public QueueStatus status=QueueStatus.WAITING;
        @Column(nullable=false) public LocalDate visitDate;
        @Column(nullable=false) public Instant joinedAt=Instant.now();
        public Instant startedAt;
        public Instant completedAt;
        @Column(length=500) public String cancellationReason;
        public Integer rating;
        @Column(length=1000) public String reviewComment;
        protected QueueEntry() {}
        public QueueEntry(Organization organization,ServiceItem service,User customer,String name,String email,String code,int number,LocalDate date){this.organization=organization;this.service=service;this.customer=customer;this.customerName=name;this.customerEmail=email;this.trackingCode=code;this.queueNumber=number;this.visitDate=date;}
    }

    @Entity @Table(name="appointment_messages", indexes=@Index(name="idx_messages_queue_created",columnList="queue_entry_id,created_at"))
    public static class AppointmentMessage {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="queue_entry_id",nullable=false) public QueueEntry queueEntry;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public User author;
        @Column(nullable=false,length=1000) public String body;
        @Column(nullable=false) public Instant createdAt=Instant.now();
        protected AppointmentMessage() {}
        public AppointmentMessage(QueueEntry queueEntry,User author,String body){this.queueEntry=queueEntry;this.author=author;this.body=body;}
    }

    @Entity @Table(name="payment_orders",indexes=@Index(name="idx_payment_user_status",columnList="user_id,status"))
    public static class PaymentOrder {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        @ManyToOne(optional=false,fetch=FetchType.EAGER) public User user;
        @Column(nullable=false,length=40,unique=true) public String razorpayOrderId;
        @Column(nullable=false,length=40) public String planCode;
        @Column(nullable=false) public int amountPaise;
        @Column(nullable=false) public int durationDays;
        @Column(nullable=false,length=20) public String status="CREATED";
        @Column(length=40) public String paymentId;
        @Column(nullable=false) public Instant createdAt=Instant.now();
        protected PaymentOrder() {}
        public PaymentOrder(User user,String razorpayOrderId,String planCode,int amountPaise,int durationDays){this.user=user;this.razorpayOrderId=razorpayOrderId;this.planCode=planCode;this.amountPaise=amountPaise;this.durationDays=durationDays;}
    }
}
