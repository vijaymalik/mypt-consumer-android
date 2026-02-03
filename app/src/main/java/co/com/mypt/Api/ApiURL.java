package co.com.mypt.Api;

public class ApiURL {
    //public static String Baseurl= "https://mobileapp.mypt-me.com/api/"; //Live URL
    public static String Baseurl= "https://mobileappuat.mypt-me.com/api/"; //Test URL
    public static String login = Baseurl+"login"; //param: country_code,phone,type
    public static String submitOTP = Baseurl+"submit-otp"; //param: country_code,phone,type,otp
    public static String resendOTP = Baseurl+"resendotp"; //param: country_code,phone
    public static String addName = Baseurl+"add-name?"; //param: name, id
    public static String preferences = Baseurl+"get-prefrences?";
    public static String goals = Baseurl+"get-goals?";
    public static String addPreferences = Baseurl+"add/prefrence?"; //ids
    public static String addGoals = Baseurl+"add/goal?"; //ids
    public static String addDOB = Baseurl+"add/dob?"; //gender
    public static String addGender = Baseurl+"add/gender?"; //gender
    public static String addWeight = Baseurl+"add/weight?"; //weight
    public static String addHeight = Baseurl+"add/height?"; //height
    public static String addLocation = Baseurl+"add/location?"; //long,lat,address
    public static String addPreferWork = Baseurl+"preferwork?"; //name
    public static String workouts = Baseurl+"workouts"; //name
    public static String make_favourite = Baseurl+"make-favourite"; //name
    public static String getTrainer = Baseurl+"get-trainer?is_filter="; //name
    public static String trainer_Detail = Baseurl+"trainer-details?trainer_id="; //name
    public static String get_availability= Baseurl+"get-availability?type="; //name
    public static String gym_select_trainer= Baseurl+"select/gym?id="; //name
    public static String studiodetails= Baseurl+"studio-details?id="; //name
    public static String getaddress= Baseurl+"get-address"; //name
    public static String get_cities= Baseurl+"get-cities"; //name
    public static String addaddress= Baseurl+"add-address"; //name
    public static String getslots= Baseurl+"get-slots?trainer_id="; //name
    public static String bookslot= Baseurl+"book-slot"; //name
    public static String getavailabilityfromJourney= Baseurl+"get-availability?trainer_id="; //name
    public static String packagecreate= Baseurl+"package-create?package_type="; //name
    public static String package_setdate= Baseurl+"package-setdate?package_type="; //name
    public static String getMember= Baseurl+"package-group?package_type="; //name
    public static String addmember= Baseurl+"add-member"; //name
    public static String deletemember= Baseurl+"delete-member?id="; //name
    public static String packagecheckout= Baseurl+"package-checkout"; //name
    public static String upcoming_classes= Baseurl+"upcoming-classes?lat="; //name
    public static String viewallClassess= Baseurl+"viewall-classes?lat="; //name
    public static String membershipvalidity= Baseurl+"membership-validity?studio_id="; //name
    public static String review_package= Baseurl+"review-package?price="; //name
    public static String book_membership= Baseurl+"book-membership"; //name
    public static String trainer_follow= Baseurl+"trainer-follow"; //name
    public static String classdetail= Baseurl+"class-detail?lat="; //name
    public static String bookclass= Baseurl+"book-class"; //name
    public static String getbooking= Baseurl+"get-booking?type="; //name
    public static String booking_detail= Baseurl+"booking-detail?id="; //name
    public static String accept_reject= Baseurl+"accept-reject"; //name
    public static String gettrainerslot= Baseurl+"get-trainerslot?id="; //name
    public static String getallslots= Baseurl+"get-allslots?id="; //name
    public static String reschedulesession= Baseurl+"reschedule-session"; //name
    public static String cancelsession= Baseurl+"cancel-session"; //name
    public static String cancelrequest= Baseurl+"cancel-request"; //name
    public static String getresources= Baseurl+"get-resources"; //name
    public static String class_category= Baseurl+"class-category?lat="; //name
    public static String usergoals= Baseurl+"user-goals"; //name
    public static String addnutrition_calories= Baseurl+"add-nutrition-calories"; //name
    public static String getnutritiondata= Baseurl+"get-nutrition-data?id="; //name
    public static String user_meals= Baseurl+"user-meals"; //name
    public static String getgoalsdata= Baseurl+"get-goals-data"; //name
    public static String addnutritionhydration= Baseurl+"add-nutrition-hydration"; //name
    public static String glassvolume= Baseurl+"glass-volume"; //name
    public static String makecustomgoal= Baseurl+"make-custom-goal"; //name
    public static String payamount= Baseurl+"pay?amount="; //name
    public static String accountdelete= Baseurl+"account-delete"; //name
    public static String usertrainer= Baseurl+"user-trainer?lat="; //name
    public static String sociallogin= Baseurl+"social-login"; //name
    public static String userprofile= Baseurl+"user-profile"; //name
    public static String userinformation= Baseurl+"user-information"; //name
    public static String updateinformation= Baseurl+"update-information"; //name
    public static String mealsdate= Baseurl+"meals-date"; //name
    public static String mealfavourite= Baseurl+"meal-favourite"; //name
    public static String userhealth_stats= Baseurl+"user-health-stats?type="; //name
    public static String deleteprofileImage= Baseurl+"delete-user-profile-image?type="; //name
    public static String get_filter_data= Baseurl+"get-filter-data"; //name
    public static String gettrainersList= Baseurl+"get-trainers"; //name
    public static String gymtrainers= Baseurl+"gym-trainers"; //name
    public static String getworkouts= Baseurl+"get-workouts"; //name
    public static String get_trainertime= Baseurl+"get-trainer-time?booking_id="; //name
    public static String workout_types= Baseurl+"workout-types"; //name
    public static String bodyparts= Baseurl+"body-parts"; //name
    public static String checktype= Baseurl+"check-type"; //name
    public static String wokoutdetail= Baseurl+"wokout-detail?id="; //name
    public static String getRenewalPlanDetails= Baseurl+"get-plans";
    public static String upgradeTopUpPlan= Baseurl+"upgrade-plan?type=";
    public static String workoutstart= Baseurl+"workout-start";
    public static String exercisecomplete= Baseurl+"exercise-complete";
    public static String workoutcomplete= Baseurl+"workout-complete";
    public static String myworkouts= Baseurl+"my-workouts?date=";
    public static String getuserstreak= Baseurl+"get-user-streak";
    public static String getexercises= Baseurl+"get-exercises";
    public static String createworkout= Baseurl+"createworkout";
    public static String seteditworkout= Baseurl+"set-edit-workout?workout_id=";
    public static String remindworkouttime= Baseurl+"remindworkouttime";
    public static String workoutdelete= Baseurl+"workout-delete?id=";
    public static String editworkoutexercise= Baseurl+"edit-workout-exercise";
    public static String deleteworkoutexercise= Baseurl+"delete-workout-exercise?workout_exercise_id=";
    public static String workout_summary= Baseurl+"get-workout-summary?session_id=";
    public static String rateTrainer= Baseurl+"trainer-review"; //trainer_id,booking_id,message,rating
    public static String upgradeTopUpMakePayment= Baseurl+"make-payment";//transaction_id,id,type,price,sessions,days
    public static String reviewUpgradeTopUpPkg = Baseurl+"review-upgrade-package";//type,id,price,sessions,days
    public static String getActiveSession = Baseurl+"get-session-detail?booking_id=";
    public static String getTrainerGroup = Baseurl+"get-trainer-group?";
    public static String getBestPlan = Baseurl+"best-plans?";
    public static String getBuddyMember = Baseurl+"member-buddy-get";
    public static String reviewPackageCheckout = Baseurl+"review-package-checkout";
}

