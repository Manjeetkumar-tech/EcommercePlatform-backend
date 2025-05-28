import { mount, flushPromises } from '@vue/test-utils';
import UserDashboard from '@/components/UserDashboard.vue';
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
}));

// jest-localstorage-mock is configured in jest.config.js and will mock localStorage

describe('UserDashboard.vue', () => {
  let wrapper;

  beforeEach(() => {
    // Reset mocks and localStorage before each test
    axios.get.mockClear();
    axios.post.mockClear(); // Just in case
    mockPush.mockClear();
    localStorage.clear();
    jest.clearAllMocks(); // Clears all mocks, including jest-localstorage-mock's setItem, getItem, removeItem etc.
  });

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount();
    }
  });

  it('redirects to /login if no token is found in localStorage', () => {
    localStorage.getItem.mockReturnValueOnce(null); // Simulate no token

    wrapper = mount(UserDashboard);

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(mockPush).toHaveBeenCalledTimes(1);
    expect(mockPush).toHaveBeenCalledWith('/login');
  });

  it('fetches and displays products when a token is present', async () => {
    const mockProducts = [
      { id: 1, name: 'Product A', price: 100 },
      { id: 2, name: 'Product B', price: 150 },
    ];
    localStorage.getItem.mockReturnValueOnce('fake-token'); // Simulate token presence
    axios.get.mockResolvedValue({ data: mockProducts });

    wrapper = mount(UserDashboard);

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(axios.get).toHaveBeenCalledTimes(1);
    expect(axios.get).toHaveBeenCalledWith('http://localhost:8080/api/products');

    // Wait for promises to resolve and DOM to update
    await flushPromises();

    const productCards = wrapper.findAll('.product-card');
    expect(productCards.length).toBe(mockProducts.length);
    expect(wrapper.text()).toContain('Product A');
    expect(wrapper.text()).toContain('$100');
    expect(wrapper.text()).toContain('Product B');
    expect(wrapper.text()).toContain('$150');
    expect(wrapper.find('.loading-message').exists()).toBe(false);
    expect(wrapper.find('.error-message').exists()).toBe(false);
  });

  it('shows a loading message initially then displays no products available if API returns empty array', async () => {
    localStorage.getItem.mockReturnValueOnce('fake-token');
    axios.get.mockResolvedValue({ data: [] }); // Empty products array

    wrapper = mount(UserDashboard);
    
    // Initially, loading should be true
    expect(wrapper.find('.loading-message').exists()).toBe(true);
    expect(wrapper.find('.loading-message').text()).toBe('Loading products...');

    await flushPromises(); // Resolve API call

    expect(axios.get).toHaveBeenCalledTimes(1);
    expect(wrapper.find('.loading-message').exists()).toBe(false);
    expect(wrapper.find('.no-products-message').exists()).toBe(true);
    expect(wrapper.find('.no-products-message').text()).toBe('No products available.');
    expect(wrapper.findAll('.product-card').length).toBe(0);
  });

  it('handles product fetch error and displays an error message', async () => {
    localStorage.getItem.mockReturnValueOnce('fake-token');
    axios.get.mockRejectedValue(new Error('Network Error'));

    wrapper = mount(UserDashboard);

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(axios.get).toHaveBeenCalledTimes(1);
    expect(axios.get).toHaveBeenCalledWith('http://localhost:8080/api/products');
    
    // Initial loading state
    expect(wrapper.find('.loading-message').exists()).toBe(true);

    await flushPromises(); // Wait for promises to resolve (error handling)

    expect(wrapper.find('.loading-message').exists()).toBe(false);
    const errorMessage = wrapper.find('.error-message');
    expect(errorMessage.exists()).toBe(true);
    expect(errorMessage.text()).toBe('Failed to load products. Please try again later.');
  });

  it('logs out the user and redirects to /login', async () => {
    localStorage.getItem.mockReturnValueOnce('fake-token'); // User is logged in
    axios.get.mockResolvedValue({ data: [] }); // Mock product fetch so component mounts fully

    wrapper = mount(UserDashboard);
    await flushPromises(); // Wait for product fetch

    // Sanity check: ensure user is "logged in"
    expect(mockPush).not.toHaveBeenCalledWith('/login');
    
    const logoutButton = wrapper.find('.logout-button');
    expect(logoutButton.exists()).toBe(true);

    await logoutButton.trigger('click');

    expect(localStorage.removeItem).toHaveBeenCalledTimes(2); // token and user
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    expect(localStorage.removeItem).toHaveBeenCalledWith('user');
    expect(mockPush).toHaveBeenCalledTimes(1); // Only this push after mount
    expect(mockPush).toHaveBeenCalledWith('/login');
  });
});
