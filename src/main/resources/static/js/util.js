async function getClerkToken() {
  if (!window.Clerk) {
    console.error("Clerk not loaded!");
    return null;
  }

  await window.Clerk.load();
  const session = window.Clerk.session;

  if (!session) {
    window.location.href = "login.html";
    return null;
  }

  return await session.getToken({
    template: "Custom_Claims"
  });
}

//logout function
document.getElementById("logoutBtn").addEventListener("click", async () => {
  await window.Clerk.load();
  await window.Clerk.signOut();
  localStorage.clear();
  document.cookie.split(";").forEach(function(c) {
    document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
  });
  window.location.href = "login.html";
});

async function getUserImage() {
  if (!window.Clerk) {
    console.error("Clerk not loaded!");
    return null;
  }

  await window.Clerk.load();
  const user = window.Clerk.user;
  console.log("Clerk user:", user.publicMetadata);
  if (user) {
    try {
      if (user.publicMetadata.role !== 'ADMIN') {
        window.location.href = "index.html";
      }
    } catch (error) {
      console.error("Error fetching user metadata:", error);
      //window.location.href = "index.html";
    }
    document.querySelector('.admin-profile img').src = user.imageUrl || 'https://avatar.iran.liara.run/public/29';
    document.querySelector('.admin-profile span').textContent = user.fullName || 'Admin User';

  }
  return null;
}

function setAdminAvatar(url) {
  const avatarImg = document.getElementById("adminAvatar");
  if (url) {
    avatarImg.src = url;
  }
}
