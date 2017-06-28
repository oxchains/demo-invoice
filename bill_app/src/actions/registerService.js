/**
 * Created by finch on 6/27/17.
 */

let name = '';
export function register(username) {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js').then(initialiseState);
    name = username;
  } else {
    console.warn('Service workers are not supported in this browser.');
  }
};

function urlB64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding)
    .replace(/\-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}

/**
 * Step two: The serviceworker is registered (started) in the browser. Now we
 * need to check if push messages and notifications are supported in the browser
 */
function initialiseState() {

  // Check if desktop notifications are supported
  if (!('showNotification' in ServiceWorkerRegistration.prototype)) {
    console.warn('Notifications aren\'t supported.');
    return;
  }

  // Check if user has disabled notifications
  // If a user has manually disabled notifications in his/her browser for
  // your page previously, they will need to MANUALLY go in and turn the
  // permission back on. In this statement you could show some UI element
  // telling the user how to do so.
  if (Notification.permission === 'denied') {
    console.warn('The user has blocked notifications.');
    return;
  }

  // Check is push API is supported
  if (!('PushManager' in window)) {
    console.warn('Push messaging isn\'t supported.');
    return;
  }

  navigator.serviceWorker.ready.then(function (serviceWorkerRegistration) {

    // Get the push notification subscription object
    serviceWorkerRegistration.pushManager.getSubscription().then(function (subscription) {

      // If this is the user's first visit we need to set up
      // a subscription to push notifications
      if (!subscription) {
        subscribe();

        return;
      }

      // Update the server state with the new subscription
      sendSubscriptionToServer(subscription);
    })
      .catch(function (err) {
        // Handle the error - show a notification in the GUI
        console.warn('Error during getSubscription()', err);
      });
  });
}


/**
 * Step three: Create a subscription. Contact the third party push server (for
 * example mozilla's push server) and generate a unique subscription for the
 * current browser.
 */
function subscribe() {
  navigator.serviceWorker.ready.then(function (serviceWorkerRegistration) {

    // Contact the third party push server. Which one is contacted by
    // pushManager is  configured internally in the browser, so we don't
    // need to worry about browser differences here.
    //
    // When .subscribe() is invoked, a notification will be shown in the
    // user's browser, asking the user to accept push notifications from
    // <yoursite.com>. This is why it is async and requires a catch.
    serviceWorkerRegistration.pushManager.getSubscription().then(function (subscription) {
      if (!!subscription) {
        refreshSubscription(serviceWorkerRegistration.pushManager, subscription)
      } else {
        pushManagerSubscribe(serviceWorkerRegistration.pushManager);
      }
    });

  });
}

function pushManagerSubscribe(pushManager) {
  pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: urlB64ToUint8Array('BFB3kkx6IXjGKhs0ExS9ysz96zQH2XG0zKv/doZV3skOUS6kkTKYaq7Vue681GKQcUR1K+um9Q1FSSL4nwIuvIk=')
  }).then(function (subscription) {
    // Update the server state with the new subscription
    return sendSubscriptionToServer(subscription);
  })
    .catch(function (e) {
      if (Notification.permission === 'denied') {
        console.warn('Permission for Notifications was denied');
      } else {
        console.error('Unable to subscribe to push.', e);
      }
    });
}


function refreshSubscription(pushManager, subscription) {
  logger.log('Refreshing subscription');
  return subscription.unsubscribe().then((bool) => {
    pushManagerSubscribe(pushManager);
  });
}


/**
 * Step four: Send the generated subscription object to our server.
 */
function sendSubscriptionToServer(subscription) {

  // Get public key and user auth from the subscription object
  var key = subscription.getKey ? subscription.getKey('p256dh') : '';
  var auth = subscription.getKey ? subscription.getKey('auth') : '';

  // This example uses the new fetch API. This is not supported in all
  // browsers yet.
  // 测试写死的API 后续调试需要将 a 参数化 每一个用户登录都不一样
  return fetch(`http://10.8.47.4:17173/bill/${name}/subscription`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      endpoint: subscription.endpoint,
      // Take byte[] and turn it into a base64 encoded string suitable for
      // POSTing to a server over HTTP
      key: key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
      auth: auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
    })
  });
}

