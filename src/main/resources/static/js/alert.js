function showMessage(message, typeMessage = "info") {
    Swal.fire({
        toast: true,
        position: 'top-end',
        text: message,
        icon: typeMessage,
        timer: 2000,
        showConfirmButton: false
    });
}
