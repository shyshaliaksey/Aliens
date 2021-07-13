package by.shyshaliaksey.webproject.model.service.impl;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import by.shyshaliaksey.webproject.controller.command.Feedback;
import by.shyshaliaksey.webproject.exception.DaoException;
import by.shyshaliaksey.webproject.exception.ServiceException;
import by.shyshaliaksey.webproject.model.dao.AlienDao;
import by.shyshaliaksey.webproject.model.dao.DaoProvider;
import by.shyshaliaksey.webproject.model.dao.UserDao;
import by.shyshaliaksey.webproject.model.entity.Alien;
import by.shyshaliaksey.webproject.model.entity.Role;
import by.shyshaliaksey.webproject.model.entity.User;
import by.shyshaliaksey.webproject.model.service.AdminService;
import by.shyshaliaksey.webproject.model.service.ServiceProvider;
import by.shyshaliaksey.webproject.model.service.TimeService;
import by.shyshaliaksey.webproject.model.service.UtilService;
import by.shyshaliaksey.webproject.model.service.ValidationService;
import by.shyshaliaksey.webproject.model.localization.LocaleKey;
import jakarta.servlet.http.Part;

public class AdminServiceImpl implements AdminService {

	private static final String IMAGE_PREFIX = "alien_image_";

	@Override
	public Map<Feedback.Key, Object> banUser(String userLogin, String daysToBan) throws ServiceException {
		Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
		ValidationService validationService = ServiceProvider.getInstance().getValidationService();
		result.put(Feedback.Key.LOGIN_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
		result.put(Feedback.Key.DAYS_TO_BAN_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
		result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
		result.put(Feedback.Key.DAYS_TO_BAN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
		try {
			if (validationService.validateLogin(userLogin)) {
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID.getValue());
			}
			int daysToBanInt = -1;
			try {
				daysToBanInt = Integer.parseInt(daysToBan);
			} catch (NumberFormatException e) {
				// TODO nothing to do here
			}
			if (validationService.validateDaysToBan(daysToBanInt)) {
				result.put(Feedback.Key.DAYS_TO_BAN_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
				result.put(Feedback.Key.DAYS_TO_BAN_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.DAYS_TO_BAN_FEEDBACK, LocaleKey.DAYS_TO_BAN_FEEDBACK_INVALID.getValue());

			}
			if (Boolean.TRUE.equals(result.get(Feedback.Key.LOGIN_STATUS)) && Boolean.TRUE.equals(result.get(Feedback.Key.DAYS_TO_BAN_STATUS))) {
				UserDao userDao = DaoProvider.getInstance().getUserDao();
				Optional<User> user = userDao.findByLogin(userLogin);
				if (user.isPresent()) {
					TimeService timeService = ServiceProvider.getInstance().getTimeService();
					String banDate = timeService.prepareBanDate(daysToBanInt);
					boolean banUserResult = userDao.banUser(userLogin, banDate);
					if (banUserResult) {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.OK);
						result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
						result.put(Feedback.Key.DAYS_TO_BAN_STATUS, Boolean.TRUE);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
						result.put(Feedback.Key.DAYS_TO_BAN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
						result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.DAYS_TO_BAN_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.STANDARD_LOGIN_FEEDBACK.getValue());
						result.put(Feedback.Key.DAYS_TO_BAN_FEEDBACK, LocaleKey.STANDARD_DAYS_TO_BAN_FEEDBACK.getValue());
					}
				} else {
					result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
					result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
					result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_USER_NOT_EXIST.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when banning user " + userLogin + " :" + e.getMessage(), e);
		}
	}

	@Override
	public Map<Feedback.Key, Object> unbanUser(String userLogin) throws ServiceException {
		try {
			Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
			UserDao userDao = DaoProvider.getInstance().getUserDao();
			ValidationService validationService = ServiceProvider.getInstance().getValidationService();
			TimeService timeService = ServiceProvider.getInstance().getTimeService();
			result.put(Feedback.Key.LOGIN_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			if (validationService.validateLogin(userLogin)) {
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
				result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID.getValue());
			}
			if (Boolean.TRUE.equals(result.get(Feedback.Key.LOGIN_STATUS))) {
				Optional<User> user = userDao.findByLogin(userLogin);
				if (user.isPresent()) {
					String unbanDate = timeService.prepareBanDate(0);
					boolean unbanUserResult = userDao.unbanUser(userLogin, unbanDate);
					if (unbanUserResult) {
						result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.OK);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.STANDARD_LOGIN_FEEDBACK.getValue());
					}
				} else {
					result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
					result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_USER_NOT_EXIST.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when unbanning user " + userLogin + " :" + e.getMessage(), e);
		}
	}

	@Override
	public Map<Feedback.Key, Object> promoteUser(String userLogin, String currentUserLogin) throws ServiceException {
		try {
			ValidationService validationService = ServiceProvider.getInstance().getValidationService();
			UserDao userDao = DaoProvider.getInstance().getUserDao();
			Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
			result.put(Feedback.Key.LOGIN_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			if (validationService.validateLogin(userLogin)) {
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID.getValue());
			}
			if (Boolean.TRUE.equals(result.get(Feedback.Key.LOGIN_STATUS))) {
				if (!userLogin.equals(currentUserLogin)) {
					Optional<User> user = userDao.findByLogin(userLogin);
					if (user.isPresent() && user.get().getRole() == Role.USER) {
						boolean promotingResult = userDao.promoteUser(userLogin);
						if (promotingResult) {
							result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
							result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
						} else {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
							result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.STANDARD_LOGIN_FEEDBACK.getValue());
						}
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
						result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_CAN_NOT_FIND_USER_FOR_PROMOTING.getValue());
					}
				} else {
					result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
					result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
					result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_PROMOTE_YOURSELF.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when promoting user " + userLogin + " :" + e.getMessage(), e);
		}
	}

	@Override
	public Map<Feedback.Key, Object> demoteAdmin(String adminLogin, String currentAdminLogin) throws ServiceException {
		try {
			UserDao userDao = DaoProvider.getInstance().getUserDao();
			ValidationService validationService = ServiceProvider.getInstance().getValidationService();
			Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
			result.put(Feedback.Key.LOGIN_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			if (validationService.validateLogin(adminLogin)) {
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
				result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID.getValue());
			}
			if (Boolean.TRUE.equals(result.get(Feedback.Key.LOGIN_STATUS))) {
				if (!adminLogin.equals(currentAdminLogin)) {
					Optional<User> user = userDao.findByLogin(adminLogin);
					if (user.isPresent() && user.get().getRole() == Role.ADMIN) {
						boolean demotingResult = userDao.demoteAdmin(adminLogin);
						if (demotingResult) {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.OK);
							result.put(Feedback.Key.LOGIN_STATUS, Boolean.TRUE);
							result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
						} else {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
							result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.STANDARD_LOGIN_FEEDBACK.getValue());
						}
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
						result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_CAN_NOT_FIND_ADMIN_FOR_DEMOTING.getValue());
					}
				} else {
					result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
					result.put(Feedback.Key.LOGIN_STATUS, Boolean.FALSE);
					result.put(Feedback.Key.LOGIN_FEEDBACK, LocaleKey.LOGIN_FEEDBACK_INVALID_DEMOTE_YOURSELF.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when demoting admin " + adminLogin + " :" + e.getMessage(), e);
		}
	}

	@Override
	public Map<Feedback.Key, Object> addNewAlien(String alienName, String alienSmallDescription,
			String alienFullDescription, Part alienImage, String rootFolder, String serverDeploymentPath)
			throws ServiceException {
		try {
			UtilService utilService = ServiceProvider.getInstance().getUtilService();
			ValidationService validationService = ServiceProvider.getInstance().getValidationService();
			AlienDao alienDao = DaoProvider.getInstance().getAlienDao();
			Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
			result.put(Feedback.Key.ALIEN_NAME_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.IMAGE_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			if (validationService.validateAlienName(alienName)) {
				result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.ALIEN_NAME_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (validationService.validateAlienSmallDescription(alienSmallDescription)) {
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.ALIEN_SMALL_DESCRIPTION_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (validationService.validateAlienFullDescription(alienFullDescription)) {
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.ALIEN_FULL_DESCRIPTION_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (alienImage != null
					&& validationService
							.validateImageExtension(FilenameUtils.getExtension(alienImage.getSubmittedFileName()))
					&& validationService.validateImageSize(alienImage.getSize())) {
				result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
			} else {
				result.put(Feedback.Key.IMAGE_STATUS, Boolean.TRUE);
				result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.IMAGE_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}

			if (Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_NAME_STATUS)) 
					&& Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS))
					&& Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS))
					&& Boolean.TRUE.equals(result.get(Feedback.Key.IMAGE_STATUS))) {
				Optional<Alien> alienInDatabase = alienDao.findByName(alienName);
				if (!alienInDatabase.isPresent()) {
					
					Optional<String> urlResult = utilService.uploadAlienImage(alienName, IMAGE_PREFIX, rootFolder, serverDeploymentPath, alienImage);
					if (urlResult.isPresent()) {
						boolean addResult = alienDao.addNewAlien(alienName, alienSmallDescription, alienFullDescription,
								urlResult.get());
						if (addResult) {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.OK);
							result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
							result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
						} else {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
							result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						}
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
						result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
					}
				} else {
					result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
					result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.ALIEN_NAME_FEEDBACK_INVALID_ALREADY_EXISTS.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when adding new alien " + alienName + " :" + e.getMessage(), e);
		}
	}

	@Override
	public Map<Feedback.Key, Object> updateAlien(int alienId, String alienName, String alienSmallDescription,
			String alienFullDescription, Part alienImage, String rootFolder, String serverDeploymentPath)
			throws ServiceException {
		try {
			UtilService utilService = ServiceProvider.getInstance().getUtilService();
			ValidationService validationService = ServiceProvider.getInstance().getValidationService();
			AlienDao alienDao = DaoProvider.getInstance().getAlienDao();
			Map<Feedback.Key, Object> result = new EnumMap<>(Feedback.Key.class);
			result.put(Feedback.Key.ALIEN_NAME_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.IMAGE_STATUS, LocaleKey.EMPTY_MESSAGE.getValue());
			result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.EMPTY_MESSAGE.getValue());
			if (validationService.validateAlienName(alienName)) {
				result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.ALIEN_NAME_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (validationService.validateAlienSmallDescription(alienSmallDescription)) {
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.ALIEN_SMALL_DESCRIPTION_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (validationService.validateAlienFullDescription(alienFullDescription)) {
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.TRUE);
			} else {
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
				result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.ALIEN_FULL_DESCRIPTION_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}
			if (alienImage != null
					&& validationService
							.validateImageExtension(FilenameUtils.getExtension(alienImage.getSubmittedFileName()))
					&& validationService.validateImageSize(alienImage.getSize())) {
				result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
			} else {
				result.put(Feedback.Key.IMAGE_STATUS, Boolean.TRUE);
				result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.IMAGE_FEEDBACK_INVALID.getValue());
				result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
			}

			if (Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_NAME_STATUS)) 
					&& Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS))
					&& Boolean.TRUE.equals(result.get(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS))
					&& Boolean.TRUE.equals(result.get(Feedback.Key.IMAGE_STATUS))) {
				Optional<Alien> alienInDatabase = alienDao.findByName(alienName);
				if (!alienInDatabase.isPresent()) {
					Optional<String> urlResult = utilService.uploadAlienImage(alienName, IMAGE_PREFIX, rootFolder, serverDeploymentPath, alienImage);
					if (urlResult.isPresent()) {
						boolean addResult = alienDao.updateAlien(alienId, alienName, alienSmallDescription,
								alienFullDescription, urlResult.get());
						if (addResult) {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.OK);
							result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						} else {
							result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
							result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
							result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
							result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						}
					} else {
						result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.INTERNAL_SERVER_ERROR);
						result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.IMAGE_STATUS, Boolean.FALSE);
						result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.ALIEN_SMALL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.ALIEN_FULL_DESCRIPTION_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
						result.put(Feedback.Key.IMAGE_FEEDBACK, LocaleKey.INTERNAL_SERVER_ERROR.getValue());
					}
				} else {
					result.put(Feedback.Key.RESPONSE_CODE, Feedback.Code.WRONG_INPUT);
					result.put(Feedback.Key.ALIEN_NAME_STATUS, Boolean.FALSE);
					result.put(Feedback.Key.ALIEN_NAME_FEEDBACK, LocaleKey.ALIEN_NAME_FEEDBACK_INVALID_DOES_NOT_EXIST.getValue());
				}
			}
			return result;
		} catch (DaoException e) {
			throw new ServiceException("Error occured when adding new alien " + alienName + " :" + e.getMessage(), e);
		}
	}

}
