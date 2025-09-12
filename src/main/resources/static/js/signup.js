async function handleCreateAccount(event) {
  event.preventDefault();
  const fullName = document.getElementById('fullName').value;
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  const password1 = document.getElementById('password1').value;

  if (password1 !== password) {
    notyf.error({ message: 'Passwords do not match', duration: 3000 });
    return;
  }

  try {
    const response = await fetch("https://api.clerk.com/v1/users", {
      method: "POST",
      headers: {
        "Authorization": "Bearer pk_test_dG9wLWdhbm5ldC05Mi5jbGVyay5hY2NvdW50cy5kZXYk", // Clerk secret
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        email_address: [email],
        password: password,
        first_name: fullName
      })
    });

    const data = await response.json();
    console.log("Clerk register:", data);

    if (response.ok) {
      notyf.success({ message: "Register successfully", duration: 3000 });
      window.location.href = "login.html";
    } else {
      notyf.error({ message: data.errors?.[0]?.message || "Error", duration: 3000 });
    }
  } catch (error) {
    console.error("Error:", error);
    notyf.error({ message: "Something went wrong", duration: 3000 });
  }
}
