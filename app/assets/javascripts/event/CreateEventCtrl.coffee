class CreateEventCtrl

  constructor: (@$log, @$location, @EventService) ->
    @$log.debug "constructing CreateEventController"
    @event = {}

  createEvent: () ->
    @$log.debug "createEvent() with userId #{userId}"
    if(userId is "")
      @$log.debug "not logged in"
      return
    @event.userId = userId
    @EventService.createEvent(@event)
    .then(
      (data) =>
        @$log.debug "Promise returned #{data} event"
        @event = data
        @$location.path("/")
    ,
      (error) =>
        @$log.error "Unable to create Event: #{error}"
        @event.error = error
    )

controllersModule.controller('CreateEventCtrl', CreateEventCtrl)