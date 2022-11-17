package com.tennisFriends.event;

import com.tennisFriends.account.CurrentUser;
import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Enrollment;
import com.tennisFriends.domain.Event;
import com.tennisFriends.domain.Lesson;
import com.tennisFriends.event.form.EventForm;
import com.tennisFriends.event.validator.EventValidator;
import com.tennisFriends.lesson.LessonRepository;
import com.tennisFriends.lesson.LessonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/lesson/{path}")
@RequiredArgsConstructor
public class EventController {

    private final LessonService lessonService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final LessonRepository lessonRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Lesson lesson = lessonService.getLessonToUpdateStatus(account, path);
        model.addAttribute(lesson);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Lesson lesson = lessonService.getLessonToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(lesson);
            return "event/form";
        }
        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), lesson, account);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(lessonRepository.findLessonOnlyByPath(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewLessonEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
        Lesson lesson = lessonService.getLesson(path);
        model.addAttribute(lesson);
        model.addAttribute(account);

        List<Event> events = eventRepository.findByLessonOrderByStartDateTime(lesson);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        return "lesson/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account, @PathVariable String path,
                                  @PathVariable("id") Event event, Model model) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        model.addAttribute(lesson);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                    @PathVariable("id") Event event, @Valid EventForm eventForm, Errors errors, Model model) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        eventForm.setEventType(eventForm.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(lesson);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Lesson lesson = lessonService.getLessonToUpdateStatus(account, path);
        eventService.deleteEvent(event);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentUser Account account, @PathVariable String path,
                                @PathVariable("id") Event event) {
        Lesson lesson = lessonService.getLessonToEnroll(path);
        eventService.newEnrollment(event, account);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("id") Event event) {
        Lesson lesson = lessonService.getLessonToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentUser Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Lesson lesson = lessonService.getLessonToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/lesson/" + lesson.getEncodePath() + "/events/" + event.getId();
    }

}
