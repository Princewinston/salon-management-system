function isAppointmentFuture(appt) {
    if (!appt.appointmentDate || !appt.appointmentTime) return false;

    // Combine date and time
    // format: YYYY-MM-DD and HH:MM:SS or HH:MM
    const dateTimeStr = `${appt.appointmentDate}T${appt.appointmentTime}`;
    const apptDate = new Date(dateTimeStr);
    const now = new Date();

    return apptDate > now;
}
