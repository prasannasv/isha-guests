'use strict';

function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();

  $.post("/validate_id_token",
         {"id_token": googleUser.getAuthResponse().id_token}
  ).done(function (res) {
    console.log("response:", res);
    if (res.isSuccess) {
      window.location.href = res.redirectPath;
    } else {
      alert("Couldn't validate id token", res.errorMessage);
    }
  });
}

function onSignInFailure(error) {
  console.log(error);
  alert(error);
}
