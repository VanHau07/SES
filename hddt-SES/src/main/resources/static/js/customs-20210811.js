$(function () {
  'use strict'
  $(function () {
    $('.preloader').fadeOut()
  }),
    jQuery(document).on('click', '.mega-dropdown', function (i) {
      i.stopPropagation()
    })
    var height_diff = $( window ).height() - $( 'body' ).height();
    if ( height_diff > 0 ) {
        $( '.footer' ).css( 'margin-top', height_diff );
    }
  var i = function () {
    ;(window.innerWidth > 0 ? window.innerWidth : this.screen.width) < 1170
      ? ($('body').addClass('mini-sidebar'),
        $('.navbar-brand span').hide(),
        $('.sidebartoggler i').addClass('ti-menu'))
      : ($('body').removeClass('mini-sidebar'),
        $('.navbar-brand span').show(),
        $('.sidebartoggler i').removeClass('ti-menu'))
     var i =
       (window.innerHeight > 0 ? window.innerHeight : this.screen.height) - 1
     ;(i -= 70) < 1 && (i = 1),
       i > 70 && $('.page-wrapper').css('min-height', i - 20 + 'px')
  }
  $(window).ready(i),
    $(window).on('resize', i),
    $('.sidebartoggler').on('click', function () {
      $('body').hasClass('mini-sidebar')
        ? ($('body').trigger('resize'),
          $('.scroll-sidebar, .slimScrollDiv')
            .css('overflow', 'hidden')
            .parent()
            .css('overflow', 'visible'),
          $('body').removeClass('mini-sidebar'),
          $('.navbar-brand span').show(),
          $('.sidebartoggler i').addClass('ti-menu'))
        : ($('body').trigger('resize'),
          $('.scroll-sidebar, .slimScrollDiv')
            .css('overflow-x', 'visible')
            .parent()
            .css('overflow', 'visible'),
          $('body').addClass('mini-sidebar'),
          $('.navbar-brand span').hide(),
          $('.sidebartoggler i').removeClass('ti-menu'))
    }),
    $('.overlay-wrap').click(function (e) {
      $('body').removeClass('show-sidebar')
    })
//    $('.icon-open__chat').click(function (e) {
//      $('body').toggleClass('show-chat')
//    })
  $('i.mdi-chevron-double-left').click(function (e) {
    $('body').removeClass('show-sidebar')
  })
  // $('.fix-header .topbar').stick_in_parent({}),
  $('.floating-labels .form-control')
    .on('focus blur', function (i) {
      $(this)
        .parents('.form-group')
        .toggleClass('focused', 'focus' === i.type || this.value.length > 0)
    })
    .trigger('blur'),
    $('.nav-toggler').click(function (e) {
      e.preventDefault()
      $('body').toggleClass('show-sidebar'),
        $('.nav-toggler i').addClass('mdi mdi-chevron-double-right')
      // $('.nav-toggler i').addClass('ti-close')
    }),
    $('.sidebartoggler').on('click', function () {}),
    $('.search-box a, .search-box .app-search .srh-btn').on(
      'click',
      function () {
        $('.app-search').toggle(200)
      }
    ),
    $('.right-side-toggle').click(function () {
      $('.right-sidebar').slideDown(50),
        $('.right-sidebar').toggleClass('shw-rside')
    }),
    $(function () {
      for (
        var i = window.location,
          e = $('ul#sidebarnav a')
            .filter(function () {
              return this.href == i
            })
            .addClass('active')
            .parent()
            .addClass('active');
        e.is('li');

      )
        e = e.parent().addClass('in').parent().addClass('active')
    }),
    $(function () {
      $('[data-toggle="tooltip"]').tooltip()
    }),
    $(function () {
      $('[data-toggle="popover"]').popover()
    }),
    $(function () {
      $('#sidebarnav').metisMenu()
    }),
    $('.message-center').slimScroll({
      position: 'right',
      size: '5px',
      color: '#dcdcdc',
    }),
    $('.aboutscroll').slimScroll({
      position: 'right',
      size: '5px',
      height: '80',
      color: '#dcdcdc',
    }),
    $('.message-scroll').slimScroll({
      position: 'right',
      size: '5px',
      height: '570',
      color: '#dcdcdc',
    }),
    $('.chat-box').slimScroll({
      position: 'right',
      size: '5px',
      height: '470',
      color: '#dcdcdc',
    }),
    $('.slimscrollright').slimScroll({
      height: '100%',
      position: 'right',
      size: '5px',
      color: '#dcdcdc',
    }),
    $('body').trigger('resize'),
    $('.list-task li label').click(function () {
      $(this).toggleClass('task-done')
    }),
    $('#to-recover').on('click', function () {
      $('#loginform').slideUp(), $('#recoverform').fadeIn()
    }),
    $('a[data-action="collapse"]').on('click', function (i) {
      i.preventDefault(),
        $(this)
          .closest('.card')
          .find('[data-action="collapse"] i')
          .toggleClass('ti-minus ti-plus'),
        $(this).closest('.card').children('.card-body').collapse('toggle')
    }),
    $('a[data-action="expand"]').on('click', function (i) {
      i.preventDefault(),
        $(this)
          .closest('.card')
          .find('[data-action="expand"] i')
          .toggleClass('mdi-arrow-expand mdi-arrow-compress'),
        $(this).closest('.card').toggleClass('card-fullscreen')
    }),
    $('a[data-action="close"]').on('click', function () {
      $(this).closest('.card').removeClass().slideUp('fast')
    }),
    $('#monthchart').sparkline([5, 6, 2, 9, 4, 7, 10, 12], {
      type: 'bar',
      height: '35',
      barWidth: '4',
      resize: !0,
      barSpacing: '4',
      barColor: '#1e88e5',
    }),
    $('#lastmonthchart').sparkline([5, 6, 2, 9, 4, 7, 10, 12], {
      type: 'bar',
      height: '35',
      barWidth: '4',
      resize: !0,
      barSpacing: '4',
      barColor: '#7460ee',
    }),
    $('.custom-file-input').on('change', function () {
      var i = $(this).val()
      $(this).next('.custom-file-label').html(i)
    })
})
