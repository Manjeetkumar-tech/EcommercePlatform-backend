import { shallowMount } from '@vue/test-utils';
import UserLogin from '@/components/UserLogin.vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

// Mock axios
jest.mock('axios');

// Mock vue-router
const mockPush = jest.fn();
jest.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush,
  }),
  // RouterLink will be stubbed in the mount options
}));

// A more robust stub for router-link
const RouterLinkStub = {
  template: '<a><slot></slot></a>',
  props: ['to']
};

// Mock localStorage (jest-localstorage-mock should handle this if setupFilesAfterEnv is working)
// If not, manual mocks might be needed:
// const localStorageMock = (function() {
//   let store = {};
//   return {
//     getItem: function(key) {
//       return store[key] || null;
//     },
//     setItem: function(key, value) {
//       store[key] = value.toString();
//     },
//     removeItem: function(key) {
//       delete store[key];
//     },
//     clear: function() {
//       store = {};
//     }
//   };
// })();
// Object.defineProperty(window, 'localStorage', { value: localStorageMock });


describe('UserLogin.vue', () => {
  let wrapper;

  beforeEach(() => {
    // Reset mocks before each test
    axios.post.mockClear();
    mockPush.mockClear();
    // localStorage.setItem.mockClear(); // if using jest.fn() for localStorage items
    // Clear localStorage mock from jest-localstorage-mock
    localStorage.clear(); 
    jest.clearAllMocks(); // Clears all mocks, including jest-localstorage-mock's setItem etc.
  });

  it('successfully logs in and redirects to dashboard', async () => {
    const mockUser = { id: 1, name: 'Test User', email: 'test@example.com' };
    const mockToken = 'fake-jwt-token';
    axios.post.mockResolvedValue({
      data: {
        success: true,
        user: mockUser,
        token: mockToken,
      },
    });

    wrapper = shallowMount(UserLogin); // Use shallowMount

    // Simulate user input
    await wrapper.find('input#email').setValue('test@example.com');
    await wrapper.find('input#password').setValue('password123');

    // Simulate form submission
    await wrapper.find('form').trigger('submit.prevent');

    // Assertions
    expect(axios.post).toHaveBeenCalledTimes(1);
    expect(axios.post).toHaveBeenCalledWith('http://localhost:8080/api/users/login', {
      email: 'test@example.com',
      password: 'password123',
    });

    // Check localStorage
    // With jest-localstorage-mock, localStorage itself is mocked
    expect(localStorage.setItem).toHaveBeenCalledTimes(2); // user and token
    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(mockUser));
    expect(localStorage.setItem).toHaveBeenCalledWith('token', mockToken);
    
    // Check redirection
    // Need to wait for promises to resolve if router.push is called in an async then() block
    await wrapper.vm.$nextTick(); // Wait for Vue to process updates
    await Promise.resolve(); // Additional tick for any chained promises
    
    expect(mockPush).toHaveBeenCalledTimes(1);
    expect(mockPush).toHaveBeenCalledWith('/dashboard');
    expect(wrapper.find('.error').exists()).toBe(false); // No error message shown
  });

  it('shows an error message on failed login', async () => {
    axios.post.mockResolvedValue({
      data: {
        success: false,
        message: 'Invalid credentials',
      },
    });

    wrapper = shallowMount(UserLogin);

    await wrapper.find('input#email').setValue('wrong@example.com');
    await wrapper.find('input#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit.prevent');

    // Assertions
    expect(axios.post).toHaveBeenCalledTimes(1);
    expect(axios.post).toHaveBeenCalledWith('http://localhost:8080/api/users/login', {
      email: 'wrong@example.com',
      password: 'wrongpassword',
    });

    // Check that error message is displayed
    // Need to wait for state updates that show the error message
    await wrapper.vm.$nextTick(); 
    
    const errorMessage = wrapper.find('.error');
    expect(errorMessage.exists()).toBe(true);
    expect(errorMessage.text()).toBe('Invalid credentials');

    // Check localStorage (should not be called on failure)
    expect(localStorage.setItem).not.toHaveBeenCalled();

    // Check redirection (should not be called on failure)
    expect(mockPush).not.toHaveBeenCalled();
  });

  it('handles network or server error during login', async () => {
    axios.post.mockRejectedValue({
      response: {
        data: { message: 'Server error occurred' }
      }
    });

    wrapper = shallowMount(UserLogin);

    await wrapper.find('input#email').setValue('test@example.com');
    await wrapper.find('input#password').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');
    
    await wrapper.vm.$nextTick(); // Allow time for error handling and state update

    const errorMessage = wrapper.find('.error');
    expect(errorMessage.exists()).toBe(true);
    expect(errorMessage.text()).toBe('Server error occurred');
    expect(localStorage.setItem).not.toHaveBeenCalled();
    expect(mockPush).not.toHaveBeenCalled();
  });
});
