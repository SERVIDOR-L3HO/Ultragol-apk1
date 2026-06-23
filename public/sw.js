self.addEventListener('push', event => {
  let data = {};
  try { data = event.data.json(); } catch { data = { title: 'Ultragol', body: event.data?.text() || '' }; }

  const icons = { update: '🚀', alert: '⚠️', info: 'ℹ️' };
  const options = {
    body:    data.body || data.message || '',
    icon:    '/icon-192.png',
    badge:   '/icon-192.png',
    tag:     'ultragol-' + (data.type || 'info'),
    renotify: true,
    data:    { url: self.registration.scope },
    actions: [{ action: 'open', title: 'Abrir panel' }],
  };

  event.waitUntil(self.registration.showNotification(data.title || 'Ultragol', options));
});

self.addEventListener('notificationclick', event => {
  event.notification.close();
  event.waitUntil(clients.openWindow(event.notification.data?.url || '/'));
});

self.addEventListener('install',  () => self.skipWaiting());
self.addEventListener('activate', e  => e.waitUntil(clients.claim()));
