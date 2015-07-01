class CreateUserCtrl

  constructor: (@$log, @$location, @UserService) ->
    @$log.debug "constructing CreateUserController"
    @user = {}

  createUser: () ->
    @$log.debug "createUser()"
    @user.active = true
    if(@user.password isnt @user.confirmPassword)
      @$log.error "password not equal"
      return

    @UserService.createUser(@user)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data} User"
        @user = data
        @$location.path("/")
    ,
      (error) =>
        @$log.error "Unable to create User: #{error}"
        @user.error = error
    )

controllersModule.controller('CreateUserCtrl', CreateUserCtrl)