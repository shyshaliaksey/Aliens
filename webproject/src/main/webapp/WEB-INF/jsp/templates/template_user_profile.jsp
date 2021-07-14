<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js'></script>
<script>
	var WEB_SITE_NAME = `${WEB_SITE_NAME}`;
	var PROJECT_NAME = `${PROJECT_NAME}`;
	var CONTROLLER = `${CONTROLLER}`;
	var COMMAND = `${COMMAND}`;
	var OPEN_HOME_PAGE = `${OPEN_HOME_PAGE}`;
	var LOAD_EMAIL_UPDATE_FORM = `${LOAD_EMAIL_UPDATE_FORM}`;
	var LOAD_IMAGE_UPDATE_FORM = `${LOAD_IMAGE_UPDATE_FORM}`;
	var LOAD_USER_IMAGE = `${LOAD_USER_IMAGE}`;
	var LOAD_PASSWORD_UPDATE_FORM = `${LOAD_PASSWORD_UPDATE_FORM}`;
	var LOAD_LOGIN_UPDATE_FORM = `${LOAD_LOGIN_UPDATE_FORM}`;
	var LOGIN = `${LOGIN}`;
	var NEW_LOGIN = `${NEW_LOGIN}`;
	var PASSWORD = `${PASSWORD}`;
	var PASSWORD_CONFIRM = `${PASSWORD_CONFIRM}`;
	var NEW_IMAGE = `${NEW_IMAGE}`;
	var EMAIL = `${EMAIL}`;
	var NEW_EMAIL = `${NEW_EMAIL}`;
	var USER_ID = `${USER_ID}`;
	var userId = `${currentUser.id}`;
	var userLogin = `${currentUser.login}`;
	var userEmail = `${currentUser.email}`;
	var UPDATE_USER_EMAIL = `${UPDATE_USER_EMAIL}`;
	var UPDATE_USER_LOGIN = `${UPDATE_USER_LOGIN}`;
	var UPDATE_USER_PASSWORD = `${UPDATE_USER_PASSWORD}`;
	var UPDATE_USER_IMAGE = `${UPDATE_USER_IMAGE}`;

	// users form
	var STANDARD_EMAIL_FEEDBACK = `${STANDARD_EMAIL_FEEDBACK}`;
	var EMAIL_STATUS = `${EMAIL_STATUS}`;
	var EMAIL_FEEDBACK = `${EMAIL_FEEDBACK}`;
	
	var STANDARD_LOGIN_FEEDBACK = `${STANDARD_LOGIN_FEEDBACK}`;
	var LOGIN_STATUS = `${LOGIN_STATUS}`;
	var LOGIN_FEEDBACK = `${LOGIN_FEEDBACK}`;	


	var STANDARD_PASSWORD_FEEDBACK = `${STANDARD_PASSWORD_FEEDBACK}`;
	var STANDARD_PASSWORD_CONFIRMATION_FEEDBACK = `${STANDARD_PASSWORD_CONFIRMATION_FEEDBACK}`;
	var PASSWORD_STATUS = `${PASSWORD_STATUS}`;
	var PASSWORD_FEEDBACK = `${PASSWORD_FEEDBACK}`;
	var PASSWORD_CONFIRMATION_STATUS = `${PASSWORD_CONFIRMATION_STATUS}`;
	var PASSWORD_CONFIRMATION_FEEDBACK = `${PASSWORD_CONFIRMATION_FEEDBACK}`;
	var PASSWORD_FEEDBACK_INVALID_PASSWORDS_ARE_NOT_EQUAL = `${PASSWORD_FEEDBACK_INVALID_PASSWORDS_ARE_NOT_EQUAL}`;

	var STANDARD_IMAGE_FEEDBACK = `${STANDARD_IMAGE_FEEDBACK}`;
	var IMAGE_FEEDBACK_INVALID = `${IMAGE_FEEDBACK_INVALID}`;
	var IMAGE_STATUS = `${IMAGE_STATUS}`;
	var IMAGE_FEEDBACK = `${IMAGE_FEEDBACK}`;
</script>
<script type="text/javascript" src="<c:url value='${JS_USER_PROFILE}'/>"></script>
<h1>${TEXT[TEMPLATE_USER_PROFILE_H1]}</h1>
<div class="content-section">
	<div class="media">
		<jsp:include page="${TEMPLATE_USER_IMAGE_JSP}"/>
		<div class="media-body">
			<h2 id="user-profile-account-login" class="account-heading text-break">${currentUser.login}</h2>
			<p id="user-profile-account-email" class="text-secondary text-break">${currentUser.email}</p>
		</div>
	</div>
	<jsp:include page="${FORM_UPDATE_EMAIL_JSP}"/>
	<jsp:include page="${FORM_UPDATE_LOGIN_JSP}"/>
	<jsp:include page="${FORM_UPDATE_IMAGE_JSP}"/>
	<jsp:include page="${FORM_UPDATE_PASSWORD_JSP}"/>
</div>

