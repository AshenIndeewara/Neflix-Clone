
let notyf = new Notyf();

function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const data = {
        "email": email,
        "password": password
    }

    console.log(data)

    fetch('https://netflix-ldox1.sevalla.app/v1/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log('POST response:', data);
            if (data.code === 200) {
                notyf.success({
                    message: 'Login successfully',
                    duration: 9000
                })
                // Set a cookie
                document.cookie = "token=" + data.data.accessToken + "; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
                window.location.href = "home.html";
            }
            else {
                notyf.error({
                    message: data.data,
                    duration: 9000
                })
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function handleCreateAccount(event) {
    event.preventDefault();
    const fullName = document.getElementById('fullName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const password1 = document.getElementById('password1').value;
    if (password1 !== password) {

        notyf.error({
            message: 'Password are not same',
            duration: 9000
        })
        return
    }

    const data = {
        "fullName": fullName,
        "email": email,
        "password": password
    }

    console.log(data)

    fetch('https://netflix-ldox1.sevalla.app/v1/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log('POST response:', data);
            if (data.code === 200) {
                notyf.success({
                    message: 'Register successfully',
                    duration: 9000
                })
                window.location.href = "login.html";
            }
            else {
                notyf.error({
                    message: data.data,
                    duration: 9000
                })
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });

}