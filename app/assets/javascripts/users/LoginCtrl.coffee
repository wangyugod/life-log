class LoginCtrl

  constructor: (@$log, @$location, @UserService) ->
    @$log.debug "constructing Login Controller"
    @user = {}

  login:() ->
    @$log.debug "login"
    @UserService.login(@user)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data} User"
        @user = data
        @$location.path("/")
    ,
      (error) =>
        @$log.error "Unable to create User: #{error}"
    )




controllersModule.controller('LoginCtrl', LoginCtrl)