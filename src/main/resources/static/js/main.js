// Main JavaScript for schedulEase

// Auto-dismiss alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

// Confirm delete actions
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this?');
}

function setMonthRange() {
  const now = new Date();
  const firstDay = new Date(now.getFullYear(), now.getMonth(), 1); // 1st of this month
  const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0); // last day of this month

  document.getElementById('startDate').value = firstDay.toISOString().split('T')[0];
  document.getElementById('endDate').value = lastDay.toISOString().split('T')[0];
  document.getElementById('reportType').value = 'bookings';
}

function setYearRange() {
  const now = new Date();
  const firstDay = new Date(now.getFullYear(), 0, 1); // Jan 1st
  const lastDay = new Date(now.getFullYear(), 11, 31); // Dec 31st

  document.getElementById('startDate').value = firstDay.toISOString().split('T')[0];
  document.getElementById('endDate').value = lastDay.toISOString().split('T')[0];
  document.getElementById('reportType').value = 'bookings';
}
